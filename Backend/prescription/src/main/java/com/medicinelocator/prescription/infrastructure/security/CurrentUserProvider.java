package com.medicinelocator.prescription.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CurrentUserProvider {

    private final GatewayHeaderExtractor headerExtractor;

    public CurrentUserProvider(GatewayHeaderExtractor headerExtractor) {
        this.headerExtractor = headerExtractor;
    }

    public CurrentUser getCurrentUser() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return headerExtractor.extract(request);
    }
}