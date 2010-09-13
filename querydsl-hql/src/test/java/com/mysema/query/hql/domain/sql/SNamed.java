package com.mysema.query.hql.domain.sql;

import static com.mysema.query.types.path.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import com.mysema.query.sql.*;
import java.util.*;


/**
 * SNamed is a Querydsl query type for SNamed
 */
@Table("NAMED")
public class SNamed extends BeanPath<SNamed> implements RelationalPath<SNamed> {

    private static final long serialVersionUID = -1646900336;

    public static final SNamed named = new SNamed("NAMED");

    public final NumberPath<Long> id = createNumber("ID", Long.class);

    public final StringPath name = createString("NAME");

    private Expression[] _all;

    public final PrimaryKey<SNamed> sql100819184435830 = new PrimaryKey<SNamed>(this, id);

    public SNamed(String variable) {
        super(SNamed.class, forVariable(variable));
    }

    public SNamed(BeanPath<? extends SNamed> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public SNamed(PathMetadata<?> metadata) {
        super(SNamed.class, metadata);
    }

    public Expression[] all() {
        if (_all == null) {
            _all = new Expression[]{id, name};
        }
        return _all;
    }

    public PrimaryKey<SNamed> getPrimaryKey() {
        return sql100819184435830;
    }

    public List<ForeignKey<?>> getForeignKeys() {
        return Collections.<ForeignKey<?>>emptyList();
    }

    public List<ForeignKey<?>> getInverseForeignKeys() {
        return Collections.<ForeignKey<?>>emptyList();
    }

    @Override
    public List<Expression<?>> getColumns() {
        return Arrays.<Expression<?>>asList(all());
    }
}

