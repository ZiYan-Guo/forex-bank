package com.forex.payment.domain.service;

import com.forex.payment.domain.event.PaymentSentEvent;
import com.forex.payment.domain.event.PaymentSubmittedEvent;
import com.forex.payment.domain.model.aggregate.CrossBorderPayment;
import com.forex.payment.domain.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.forex.common.base.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class PaymentDomainServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Captor private ArgumentCaptor<PaymentSubmittedEvent> submittedCaptor;
    @Captor private ArgumentCaptor<PaymentSentEvent> sentCaptor;

    private PaymentDomainService paymentDomainService;

    @BeforeEach
    void setUp() {
        paymentDomainService = new PaymentDomainService(paymentRepository, eventPublisher);
    }

    private CrossBorderPayment createPayment() {
        return CrossBorderPayment.create(
                1001L, "OUTWARD", "TT",
                new BigDecimal("10000.00"), "USD",
                null, null,
                "SENDER", "BENEFICIARY", null,
                "CHASUS33", "CMBCCNBS", "MT103",
                null, null, null,
                "SHA", LocalDate.now(), 1001L, "Test payment"
        );
    }

    @Test
    @DisplayName("Create payment assigns payment number and saves")
    void testCreatePayment() {
        CrossBorderPayment payment = createPayment();
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        CrossBorderPayment result = paymentDomainService.createPayment(payment);
        assertNotNull(result.getPaymentNo());
        verify(paymentRepository).save(any());
    }

    @Test
    @DisplayName("Submit payment changes status to SUBMITTED and publishes event")
    void testSubmitPayment() {
        CrossBorderPayment payment = createPayment();
        when(paymentRepository.save(any())).thenReturn(payment);
        paymentDomainService.submitPayment(payment);
        assertEquals("SUBMITTED", payment.getPaymentStatus());
        verify(eventPublisher).publishEvent(submittedCaptor.capture());
        assertEquals(payment.getPayAmount(), submittedCaptor.getValue().getAmount());
    }

    @Test
    @DisplayName("Send payment changes status to SENT and publishes event")
    void testSendPayment() {
        CrossBorderPayment payment = createPayment();
        payment.submit();
        payment.approve(null);
        payment.markAmlCheckPassed();
        when(paymentRepository.save(any())).thenReturn(payment);
        paymentDomainService.sendPayment(payment, "S20260601001", "C20260601001");
        assertEquals("SENT", payment.getPaymentStatus());
        verify(eventPublisher).publishEvent(sentCaptor.capture());
        assertEquals("S20260601001", sentCaptor.getValue().getSwiftRef());
    }

    @Test
    @DisplayName("Approve payment changes status to AML_CHECK")
    void testApprovePayment() {
        CrossBorderPayment payment = createPayment();
        payment.submit();
        when(paymentRepository.save(any())).thenReturn(payment);
        paymentDomainService.approvePayment(payment);
        assertEquals("AML_CHECK", payment.getPaymentStatus());
    }

    @Test
    @DisplayName("Process AML pass sets status to APPROVED")
    void testProcessAmlResult_Passed() {
        CrossBorderPayment payment = createPayment();
        payment.submit();
        payment.approve(null);
        when(paymentRepository.save(any())).thenReturn(payment);
        paymentDomainService.processAmlResult(payment, true, "OK");
        assertEquals("APPROVED", payment.getPaymentStatus());
    }

    @Test
    @DisplayName("Process AML reject sets status to AML_REJECTED")
    void testProcessAmlResult_Rejected() {
        CrossBorderPayment payment = createPayment();
        payment.submit();
        payment.approve(null);
        when(paymentRepository.save(any())).thenReturn(payment);
        paymentDomainService.processAmlResult(payment, false, "HIT");
        assertEquals("AML_REJECTED", payment.getPaymentStatus());
    }

    @Test
    @DisplayName("Cancel payment sets status to CANCELLED")
    void testCancelPayment() {
        CrossBorderPayment payment = createPayment();
        when(paymentRepository.save(any())).thenReturn(payment);
        paymentDomainService.cancelPayment(payment, "Customer request");
        assertEquals("CANCELLED", payment.getPaymentStatus());
    }

    @Test
    @DisplayName("Calculate fees with OUR returns 0.2%")
    void testCalculateFees_OUR() {
        BigDecimal fee = paymentDomainService.calculateFees(new BigDecimal("10000.00"), "OUR");
        assertEquals(0, fee.compareTo(new BigDecimal("20.00")));
    }

    @Test
    @DisplayName("Calculate fees with SHA returns 0.1%")
    void testCalculateFees_SHA() {
        BigDecimal fee = paymentDomainService.calculateFees(new BigDecimal("10000.00"), "SHA");
        assertEquals(0, fee.compareTo(new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Calculate settlement amount multiplies amount by rate")
    void testCalculateSettlementAmount() {
        BigDecimal result = paymentDomainService.calculateSettlementAmount(
                new BigDecimal("1000.00"), new BigDecimal("7.24"));
        assertEquals(0, result.compareTo(new BigDecimal("7240.00")));
    }

    @Test
    @DisplayName("Calculate settlement amount throws when amount is zero")
    void testCalculateSettlementAmount_ZeroAmount() {
        assertThrows(BusinessException.class,
                () -> paymentDomainService.calculateSettlementAmount(BigDecimal.ZERO, new BigDecimal("7.24")));
    }
}
