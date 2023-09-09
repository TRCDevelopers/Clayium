package com.github.trcdeveloppers.clayium.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If an item is instance of CMaterial and registryName ends with "ingot" or "dust",
 * respective models are used without this annotation.
 * However, if you want to use another specific model for an item, use this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UseModel {
    GeneralItemModel value();
}
