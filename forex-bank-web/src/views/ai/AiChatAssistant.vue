<template>
  <div class="ai-chat-assistant">
    <h2>AI智能客服</h2>
    <div class="chat-layout">
      <div class="chat-sidebar">
        <a-card title="历史会话" size="small">
          <template #extra>
            <a-button type="primary" size="small" @click="newSession">新建会话</a-button>
          </template>
          <a-list
            :data-source="sessions"
            :split="false"
            size="small"
            style="height: calc(100vh - 180px); overflow-y: auto"
          >
            <template #renderItem="{ item }">
              <a-list-item
                :class="{ active: item.id === activeSessionId }"
                style="cursor:pointer;padding:8px 12px"
                @click="switchSession(item)"
              >
                <a-list-item-meta>
                  <template #title>{{ item.title }}</template>
                  <template #description>{{ item.time }}</template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </div>

      <div class="chat-main">
        <a-card class="chat-card">
          <div class="chat-messages" ref="messagesContainer">
            <div
              v-for="(msg, idx) in currentMessages"
              :key="idx"
              :class="['msg-row', msg.role === 'user' ? 'msg-user' : 'msg-assistant']"
            >
              <div
                v-if="msg.role === 'user'"
                class="msg-bubble msg-bubble-user"
              >{{ msg.content }}</div>
              <div
                v-else
                class="msg-bubble msg-bubble-assistant"
              >
                <div v-html="renderMarkdown(msg.content)" />
                <div v-if="msg.sources && msg.sources.length" class="msg-sources">
                  <a-dropdown>
                    <a-button size="small" type="link">来源</a-button>
                    <template #overlay>
                      <a-menu>
                        <a-menu-item v-for="src in msg.sources" :key="src">
                          <span>{{ src }}</span>
                        </a-menu-item>
                      </a-menu>
                    </template>
                  </a-dropdown>
                </div>
              </div>
            </div>
            <div v-if="loading" class="msg-row msg-assistant">
              <div class="msg-bubble msg-bubble-assistant">
                <a-spin size="small" /> AI正在思考...
              </div>
            </div>
          </div>

          <div class="quick-chips">
            <a-tag
              v-for="chip in quickChips"
              :key="chip"
              color="blue"
              style="cursor:pointer;margin-right:8px"
              @click="sendQuickChip(chip)"
            >{{ chip }}</a-tag>
          </div>

          <div class="chat-input">
            <a-textarea
              v-model:value="inputText"
              :rows="3"
              placeholder="输入您的问题..."
              @pressEnter.prevent="sendMessage"
            />
            <a-button
              type="primary"
              style="margin-top:8px"
              :loading="loading"
              @click="sendMessage"
            >发送</a-button>
          </div>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { message } from 'ant-design-vue'

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  sources?: string[]
}

interface Session {
  id: string
  title: string
  time: string
  messages: ChatMessage[]
}

const activeSessionId = ref('')
const inputText = ref('')
const loading = ref(false)
const messagesContainer = ref<HTMLElement>()

const quickChips = ['结售汇政策', '信用证办理流程', '国际收支申报', '反洗钱要求']

const sessions = ref<Session[]>([
  {
    id: '1',
    title: '结售汇政策咨询',
    time: '2024-06-04 10:30',
    messages: [
      { role: 'user', content: '企业办理即期结售汇需要什么材料？' },
      {
        role: 'assistant',
        content: '企业办理即期结售汇需提供以下材料：\n\n1. **营业执照**副本复印件\n2. **对外贸易经营者备案登记表**\n3. **外汇登记证**（如需）\n4. 相关**贸易合同**或发票\n5. **结售汇申请书**\n\n具体以当地外汇管理局要求为准。',
        sources: ['外汇管理条例', '即期结售汇FAQ']
      }
    ]
  },
  {
    id: '2',
    title: '信用证办理流程',
    time: '2024-06-04 09:15',
    messages: [
      { role: 'user', content: '国际信用证怎么办理？' },
      {
        role: 'assistant',
        content: '国际信用证办理流程：\n\n1. 申请人提交**开证申请书**\n2. 银行审核**贸易背景真实性**\n3. 开立**SWIFT MT700报文**\n4. 通知行通知受益人\n5. 受益人发货并交单\n6. 银行审核单据后付款',
        sources: ['信用证办理流程FAQ', 'UCP600']
      }
    ]
  }
])

const currentMessages = ref<ChatMessage[]>(sessions.value[0]?.messages || [])
activeSessionId.value = sessions.value[0]?.id || ''

function renderMarkdown(text: string): string {
  return text
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br/>')
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

function newSession() {
  const newSess: Session = {
    id: Date.now().toString(),
    title: '新会话',
    time: new Date().toLocaleString(),
    messages: []
  }
  sessions.value.unshift(newSess)
  activeSessionId.value = newSess.id
  currentMessages.value = newSess.messages
}

function switchSession(session: Session) {
  activeSessionId.value = session.id
  currentMessages.value = session.messages
}

function sendMessage() {
  const text = inputText.value.trim()
  if (!text) return
  const session = sessions.value.find(s => s.id === activeSessionId.value)
  if (!session) return

  session.messages.push({ role: 'user', content: text })
  inputText.value = ''
  scrollToBottom()

  loading.value = true
  setTimeout(() => {
    const reply: ChatMessage = {
      role: 'assistant',
      content: '已收到您的问题：「' + text + '」。我正在为您查询相关政策文档，请稍候...\n\n为您找到了以下相关信息，如需进一步了解请继续提问。',
      sources: ['外汇管理条例', '结售汇管理规定']
    }
    session.messages.push(reply)
    currentMessages.value = [...session.messages]
    loading.value = false
    scrollToBottom()
    message.success('回复完成')
  }, 1000)
}

function sendQuickChip(chip: string) {
  inputText.value = chip
  sendMessage()
}

watch(() => currentMessages.value.length, () => scrollToBottom())
</script>

<style scoped>
.ai-chat-assistant h2 { margin-bottom: 16px; }
.chat-layout { display: flex; gap: 16px; height: calc(100vh - 140px); }
.chat-sidebar { width: 300px; flex-shrink: 0; }
.chat-sidebar :deep(.ant-list-item.active) { background: #e6f4ff; border-radius: 4px; }
.chat-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.chat-card { flex: 1; display: flex; flex-direction: column; }
.chat-card :deep(.ant-card-body) { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.chat-messages { flex: 1; overflow-y: auto; padding: 8px 0; }
.msg-row { display: flex; margin-bottom: 12px; }
.msg-user { justify-content: flex-end; }
.msg-assistant { justify-content: flex-start; }
.msg-bubble { max-width: 75%; padding: 10px 14px; border-radius: 8px; line-height: 1.6; }
.msg-bubble-user { background: #1677ff; color: #fff; }
.msg-bubble-assistant { background: #f0f0f0; color: #333; }
.msg-sources { margin-top: 6px; }
.quick-chips { padding: 8px 0; border-top: 1px solid #f0f0f0; }
.chat-input { padding-top: 8px; border-top: 1px solid #f0f0f0; }
.chat-input textarea { resize: none; }
</style>
