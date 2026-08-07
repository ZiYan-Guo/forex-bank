import { defineConfig } from "./vite-imports/vite.mjs";
import react from "./vite-imports/plugin-react.mjs";
import tailwindcss from "./vite-imports/tailwind-vite.mjs";
import path from "node:path";
import fs from "node:fs";
import crypto from "node:crypto";

const dashboardDir = "/Users/guoziyan/.understand-anything/repo/understand-anything-plugin/packages/dashboard";
const pluginRoot = "/Users/guoziyan/.understand-anything/repo/understand-anything-plugin";
const projectRoot = process.env.GRAPH_DIR || "/Users/guoziyan/Desktop/java-project/project-ai/forex-bank-system";
const uaDir = fs.existsSync(path.join(projectRoot, ".understand-anything")) ? path.join(projectRoot, ".understand-anything") : path.join(projectRoot, ".ua");
const ACCESS_TOKEN = process.env.UNDERSTAND_ACCESS_TOKEN || crypto.randomBytes(16).toString("hex");
const MAX_SOURCE_FILE_BYTES = 1024 * 1024;
function sendJson(res, statusCode, payload) { res.statusCode = statusCode; res.setHeader("Content-Type", "application/json"); res.end(JSON.stringify(payload)); }
function safeGraphFile(name) { const p = path.join(uaDir, name); return fs.existsSync(p) ? p : null; }
function normalizeGraphPath(filePath) { const raw = path.isAbsolute(filePath) ? (filePath.startsWith(projectRoot) ? path.relative(projectRoot, filePath) : null) : filePath; if (!raw) return null; const n = path.normalize(raw); if (!n || n === "." || n === ".." || n.startsWith(".." + path.sep) || path.isAbsolute(n) || n.includes("\0")) return null; return n.split(path.sep).join("/"); }
function graphFilePathSet(graphFile) { const allowed = new Set(); try { const raw = JSON.parse(fs.readFileSync(graphFile, "utf8")); for (const node of raw.nodes || []) { if (typeof node.filePath !== "string") continue; const normalized = normalizeGraphPath(node.filePath); if (normalized) allowed.add(normalized); } } catch {} return allowed; }
function detectLanguage(filePath) { const ext = path.extname(filePath).slice(1).toLowerCase(); return ({java:"java",ts:"typescript",tsx:"tsx",js:"javascript",vue:"vue",json:"json",md:"markdown",yml:"yaml",yaml:"yaml",xml:"xml",sql:"sql",html:"markup",css:"css",properties:"properties"})[ext] || "text"; }
function readSourceFile(url) { const requested = url.searchParams.get("path") || ""; if (!requested || requested.includes("\0") || path.isAbsolute(requested)) return {statusCode:400,payload:{error:"Invalid path"}}; const normalized = path.normalize(requested); if (normalized === "." || normalized === ".." || normalized.startsWith(".." + path.sep) || path.isAbsolute(normalized)) return {statusCode:400,payload:{error:"Path must stay inside the project"}}; const graphFile = safeGraphFile("knowledge-graph.json"); if (!graphFile) return {statusCode:404,payload:{error:"No knowledge graph found. Run /understand first."}}; const safeRelativePath = normalized.split(path.sep).join("/"); if (!graphFilePathSet(graphFile).has(safeRelativePath)) return {statusCode:404,payload:{error:"File is not in the knowledge graph"}}; const absoluteFile = path.resolve(projectRoot, normalized); let stat; try { stat = fs.statSync(absoluteFile); } catch { return {statusCode:404,payload:{error:"File not found"}}; } if (!stat.isFile()) return {statusCode:400,payload:{error:"Path is not a file"}}; if (stat.size > MAX_SOURCE_FILE_BYTES) return {statusCode:413,payload:{error:"File is too large to preview"}}; const buffer = fs.readFileSync(absoluteFile); if (buffer.includes(0)) return {statusCode:415,payload:{error:"Binary files cannot be previewed"}}; const content = buffer.toString("utf8"); return {statusCode:200,payload:{path:safeRelativePath,language:detectLanguage(safeRelativePath),content,sizeBytes:buffer.byteLength,lineCount:content ? content.split(/\r\n|\n|\r/).length : 0}}; }
export default defineConfig({
  root: dashboardDir,
  cacheDir: path.join(projectRoot, ".understand-anything", "tmp", "vite-cache"),
  server: { host: "127.0.0.1", port: 5173, open: false },
  resolve: { alias: {
    "@understand-anything/core/schema": path.resolve(pluginRoot, "packages/core/dist/schema.js"),
    "@understand-anything/core/search": path.resolve(pluginRoot, "packages/core/dist/search.js"),
    "@understand-anything/core/types": path.resolve(pluginRoot, "packages/core/dist/types.js")
  }},
  plugins: [react(), tailwindcss(), { name: "serve-knowledge-graph-workspace", configureServer(server) { server.httpServer?.once("listening", () => { const address = server.httpServer?.address(); const port = typeof address === "object" && address ? address.port : 5173; console.log("\n  🔑  Dashboard URL: http://127.0.0.1:" + port + "/?token=" + ACCESS_TOKEN + "\n"); }); server.middlewares.use((req,res,next)=>{ const url = new URL(req.url || "/", "http://127.0.0.1:5173"); const protectedPaths = new Set(["/knowledge-graph.json","/domain-graph.json","/diff-overlay.json","/meta.json","/config.json","/file-content.json","/staleness.json"]); if (!protectedPaths.has(url.pathname)) return next(); if (url.searchParams.get("token") !== ACCESS_TOKEN) return sendJson(res,403,{error:"Forbidden: missing or invalid token"}); if (url.pathname === "/file-content.json") { const result = readSourceFile(url); return sendJson(res,result.statusCode,result.payload); } if (url.pathname === "/staleness.json") return sendJson(res,200,{graphs:{knowledge:{status:"current"}}}); const fileName = url.pathname.slice(1); const p = safeGraphFile(fileName); if (!p) return sendJson(res, fileName === "config.json" ? 200 : 404, fileName === "config.json" ? {autoUpdate:false, outputLanguage:"zh"} : {error:"not found"}); try { return sendJson(res,200,JSON.parse(fs.readFileSync(p,"utf8"))); } catch { return sendJson(res,500,{error:"Failed to read graph file"}); } }); } }]
});
