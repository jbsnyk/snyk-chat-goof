package io.snyk.snyklabs.invoice.dto;

import java.util.List;

public class InvoiceRequestDto {
    private String invoiceNumber;
    private String customerName;
    private String customerEmail;
    private List<TransactionDto> transactions;

    public InvoiceRequestDto() {
    }

    public InvoiceRequestDto(String invoiceNumber, String customerName, String customerEmail, List<TransactionDto> transactions) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.transactions = transactions;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }
}
