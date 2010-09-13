/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.sql.mssql;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mysema.query.types.Expression;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Visitor;
import com.mysema.query.types.custom.NumberTemplate;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.NumberPath;

/**
 * RowNumber supports row_number constructs for MS SQL Server
 *
 * @author tiwe
 *
 */
public class RowNumber extends SimpleExpression<Long>{

    private static final long serialVersionUID = 3499501725767772281L;

    private final List<Expression<?>> partitionBy = new ArrayList<Expression<?>>();

    private final List<OrderSpecifier<?>> orderBy = new ArrayList<OrderSpecifier<?>>();

    @Nullable
    private NumberPath<Long> target;

    public RowNumber() {
        super(Long.class);
    }

    @Override
    public <R,C> R accept(Visitor<R,C> v, C context) {
        List<Expression<?>> args = new ArrayList<Expression<?>>(partitionBy.size() + orderBy.size());
        StringBuilder builder = new StringBuilder("row_number() over (");
        if (!partitionBy.isEmpty()){
            builder.append("partition by ");
            appendPartition(args, builder);
        }
        if (!orderBy.isEmpty()){
            if (!partitionBy.isEmpty()){
                builder.append(" ");
            }
            builder.append("order by ");
            appendOrder(args, builder);
        }
        builder.append(")");

        if (target != null){
            builder.append(" as {" + args.size() + "}");
            args.add(target);
        }

        NumberExpression<Long> expr = NumberTemplate.create(Long.class, builder.toString(),
                args.toArray(new Expression[args.size()]));
        return expr.accept(v, context);
    }

    // TODO : externalize
    private void appendPartition(List<Expression<?>> args, StringBuilder builder) {
        boolean first = true;
        for (Expression<?> expr : partitionBy){
            if (!first){
                builder.append(", ");
            }
            builder.append("{"+args.size()+"}");
            args.add(expr);
            first = false;
        }
    }

    // TODO : externalize
    private void appendOrder(List<Expression<?>> args, StringBuilder builder) {
        boolean first = true;
        for (OrderSpecifier<?> expr : orderBy){
            if (!first){
                builder.append(", ");
            }
            builder.append("{" + args.size()+"}");
            if (!expr.isAscending()){
                builder.append(" desc");
            }
            args.add(expr.getTarget());
            first = false;
        }
    }

    public RowNumber orderBy(OrderSpecifier<?>... order){
        for (OrderSpecifier<?> o : order){
            orderBy.add(o);
        }
        return this;
    }

    public RowNumber orderBy(ComparableExpression<?>... order){
        for (ComparableExpression<?> o : order){
            orderBy.add(o.asc());
        }
        return this;
    }

    public RowNumber partitionBy(Expression<?>... exprs){
        for (Expression<?> expr : exprs){
            partitionBy.add(expr);
        }
        return this;
    }

    public RowNumber as(NumberPath<Long> target){
        this.target = target;
        return this;
    }

    @Override
    public int hashCode(){
        return orderBy.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this){
            return true;
        }else if (o instanceof RowNumber){
            RowNumber rn = (RowNumber)o;
            return partitionBy.equals(rn.partitionBy) && orderBy.equals(rn.orderBy);
        }else{
            return false;
        }
    }

}
