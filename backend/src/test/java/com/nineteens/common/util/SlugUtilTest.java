package com.nineteens.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SlugUtilTest {

    @Test
    void slugifyNormalizesNames() {
        assertEquals("sand-linen-shirt", SlugUtil.slugify("Sand Linen Shirt"));
        assertEquals("nineteens", SlugUtil.slugify("  Nineteens  "));
    }
}
