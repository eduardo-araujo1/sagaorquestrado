package com.eduardo.productvalidationservice.core.service;

import com.eduardo.productvalidationservice.config.exception.ValidationException;
import com.eduardo.productvalidationservice.core.dto.Event;
import com.eduardo.productvalidationservice.core.dto.History;
import com.eduardo.productvalidationservice.core.dto.OrderProducts;
import com.eduardo.productvalidationservice.core.enums.ESagaStatus;
import com.eduardo.productvalidationservice.core.model.Validation;
import com.eduardo.productvalidationservice.core.producer.KafkaProducer;
import com.eduardo.productvalidationservice.core.repository.ProductRepository;
import com.eduardo.productvalidationservice.core.repository.ValidationRepository;
import com.eduardo.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.eduardo.productvalidationservice.core.enums.ESagaStatus.SUCCESS;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;

    public void validateExistingProducts(Event event){
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        }catch (Exception ex){
            log.error("Error trying to validate products", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void validateProductsInformed(Event event){
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())){
            throw new ValidationException("Product list is empty.");
        }
        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())){
            throw new ValidationException("OrderID and TransactionID must be informed.");
        }
    }

    private void checkCurrentValidation(Event event){
        validateProductsInformed(event);
        if (validationRepository.existsByOrderIdAndTransactionId(
                event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation");
        }
        event.getPayload().getProducts().forEach(product -> {
            validateProductInformed(product);
            validateExistingProduct(product.getProduct().getCode());
        });
    }

    private void validateProductInformed(OrderProducts product){
        if (isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode())){
            throw new ValidationException("Product must be informed.");
        }
    }

    private void validateExistingProduct(String code){
        if (!productRepository.existsByCode(code)){
            throw new ValidationException("Product does not exists in database!");
        }
    }

    private void createValidation(Event event, boolean success){
        var validation = Validation
                .builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }

    private void handleSuccess(Event event){
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event,"Products are validated successfully!");
    }

    private void addHistory(Event event, String message){
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }


}
