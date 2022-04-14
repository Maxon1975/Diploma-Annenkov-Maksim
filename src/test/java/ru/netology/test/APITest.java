package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.ConnectionManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.data.APIManager.fillForm;
import static ru.netology.data.ConnectionManager.getCreditId;
import static ru.netology.data.ConnectionManager.getPaymentId;
import static ru.netology.data.DataManager.*;

public class APITest {

    String pathToPay = "/api/v1/pay";
    String pathToCredit = "/api/v1/credit";
    int successCode = 200;
    int errorCode = 400;

    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(false));
    }

    @AfterAll
    static void tearDown() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @AfterEach
    void clean() {
        ConnectionManager.cleanDb();
    }

    @Test
    void shouldGiveResponseForValidApprovedDebitCard() {
        val validApprovedCard = getValidApprovedCard();
        val response = fillForm(validApprovedCard, pathToPay, successCode);
        assertTrue(response.contains("APPROVED"));

        val actualId = getPaymentId();
        assertNotNull(actualId);
    }

    @Test
    void shouldGiveResponseForValidApprovedCreditCard() {
        val validApprovedCard = getValidApprovedCard();
        val response = fillForm(validApprovedCard, pathToCredit, successCode);
        assertTrue(response.contains("APPROVED"));

        val actualId = getCreditId();
        assertNotNull(actualId);
    }

    @Test
    void shouldGiveResponseForValidDeclinedDebitCard() {
        val validDeclinedCard = getValidDeclinedCard();
        val response = fillForm(validDeclinedCard, pathToPay, successCode);
        assertTrue(response.contains("DECLINED"));

        val actualId = getPaymentId();
        assertNotNull(actualId);
    }

    @Test
    void shouldGiveResponseForValidDeclinedCreditCard() {
        val validDeclinedCard = getValidDeclinedCard();
        val response = fillForm(validDeclinedCard, pathToCredit, successCode);
        assertTrue(response.contains("DECLINED"));

        val actualId = getCreditId();
        assertNotNull(actualId);
    }

    @Test
    void shouldGiveResponseForInValidDebitCard() {
        val invalidCard = getEmptyNumberCard();
        val response = fillForm(invalidCard, pathToPay, errorCode);
        assertTrue(response.contains("Bad Request"));
    }

    @Test
    void shouldGiveResponseForInValidCreditCard() {
        val invalidCard = getMuchFutureYearCard();
        val response = fillForm(invalidCard, pathToCredit, errorCode);
        assertTrue(response.contains("Bad Request"));
    }
}
