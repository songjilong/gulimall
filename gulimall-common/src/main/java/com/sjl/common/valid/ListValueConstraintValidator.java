package com.sjl.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author song
 * @create 2020/4/8 17:27
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private static Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        for (int val : constraintAnnotation.vals()) {
            set.add(val);
        }
    }

    /**
     * 是否通过校验
     *
     * @param value   传入的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
