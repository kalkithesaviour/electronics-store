package com.vishal.electronicsstore.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "User ID is required!")
    private String userId;

    private String orderStatus = "PENDING";
    private String paymentStatus = "NOT_PAID";

    @NotBlank(message = "Billing address is required!")
    private String billingAddress;

    @NotBlank(message = "Billing phone number is required!")
    private String billingPhone;

    @NotBlank(message = "Billing name is required!")
    private String billingName;

}
