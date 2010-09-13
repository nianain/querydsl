/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.inheritance;

import org.junit.Test;

import com.mysema.query.annotations.QueryEntity;
import com.mysema.query.domain.AbstractTest;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.SimplePath;
import com.mysema.query.types.path.StringPath;

public class Inheritance4Test extends AbstractTest{

    @QueryEntity
    public class EntityWithComparable {
        private Comparable<?> field;

        public Comparable<?> getField() {
            return field;
        }

    }

    @QueryEntity
    public class EntityWithNumber extends EntityWithComparable{
        private Long field;

        public Long getField() {
            return field;
        }

    }

    @QueryEntity
    public class EntityWithString extends EntityWithComparable{
        private String field;

        public String getField() {
            return field;
        }

    }

    @Test
    public void test() throws SecurityException, NoSuchFieldException{
        cl = QInheritance4Test_EntityWithComparable.class;
        match(SimplePath.class, "field");

        cl = QInheritance4Test_EntityWithNumber.class;
        match(NumberPath.class, "field");

        cl = QInheritance4Test_EntityWithString.class;
        match(StringPath.class, "field");

    }
}
