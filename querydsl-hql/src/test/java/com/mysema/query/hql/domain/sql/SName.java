package com.mysema.query.hql.domain.sql;

import static com.mysema.query.types.path.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import com.mysema.query.sql.*;
import java.util.*;


/**
 * SName is a Querydsl query type for SName
 */
@Table("NAME")
public class SName extends BeanPath<SName> implements RelationalPath<SName> {

    private static final long serialVersionUID = 501063508;

    public static final SName name = new SName("NAME");

    public final StringPath firstname = createString("FIRSTNAME");

    public final NumberPath<Long> id = createNumber("ID", Long.class);

    public final StringPath lastname = createString("LASTNAME");

    public final StringPath nickname = createString("NICKNAME");

    private Expression[] _all;

    public final PrimaryKey<SName> sql100819184435150 = new PrimaryKey<SName>(this, id);

    public final ForeignKey<SCustomer> _fk27fbe3fe4707a44 = new ForeignKey<SCustomer>(this, id, "NAME_ID");

    public SName(String variable) {
        super(SName.class, forVariable(variable));
    }

    public SName(BeanPath<? extends SName> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public SName(PathMetadata<?> metadata) {
        super(SName.class, metadata);
    }

    public Expression[] all() {
        if (_all == null) {
            _all = new Expression[]{firstname, id, lastname, nickname};
        }
        return _all;
    }

    public PrimaryKey<SName> getPrimaryKey() {
        return sql100819184435150;
    }

    public List<ForeignKey<?>> getForeignKeys() {
        return Collections.<ForeignKey<?>>emptyList();
    }

    public List<ForeignKey<?>> getInverseForeignKeys() {
        return Arrays.<ForeignKey<?>>asList(_fk27fbe3fe4707a44);
    }

    @Override
    public List<Expression<?>> getColumns() {
        return Arrays.<Expression<?>>asList(all());
    }
}

