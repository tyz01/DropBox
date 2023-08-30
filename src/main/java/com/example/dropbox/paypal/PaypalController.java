package com.example.dropbox.paypal;

import com.example.dropbox.google.GoogleDriveService;
import com.example.dropbox.model.Project;
import com.example.dropbox.service.ProjectService;
import com.google.api.services.drive.model.File;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@Controller
@AllArgsConstructor
public class PaypalController {
    @Autowired
    private PaypalService service;

    private GoogleDriveService googleDriveService;

    @Autowired
    private ProjectService projectService;

    public static final String SUCCESS_URL = "pay/success";
    public static final String CANCEL_URL = "pay/cancel";

    @GetMapping("/pay")
    public String paypalHome() {
        return "home";
    }

    @PostMapping("/pay")
    public String payment(@ModelAttribute("order") Order order) {
        try {
            Payment payment = service.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
                    order.getIntent(), order.getDescription(), "http://localhost:9090/" + CANCEL_URL,
                    "http://localhost:9090/" + SUCCESS_URL);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }

        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping(value = CANCEL_URL)
    public String cancelPay() {
        return "cancel";
    }

    @GetMapping(value = SUCCESS_URL)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId,
                             @RequestParam("tariff") String selectedTariff) {
        try {
            Payment payment = service.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                Project project = projectService.createProject("Название проекта", "Описание проекта", "Google Диск", "Ссылка на папку");

                File folder = googleDriveService.createFolder("Название папки на Google Диске");

                if (folder != null) {
                    String folderId = folder.getId();

                    projectService.addAdministratorToProjectAndFolder(project.getId(), folderId);

                    return "success";
                } else {
                    return "redirect:/error";
                }
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }

}
