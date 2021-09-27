package com.example.report.util;

import com.example.report.util.ApiException;
import com.example.report.util.ErrorCodes;
import java.sql.Connection;
import java.util.Properties;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.RowBounds;


@Intercepts(
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}))
public class MybatisPaginationInterceptor implements Interceptor {

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY =
        new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY =
        new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation iv) throws Throwable {
        StatementHandler target = (StatementHandler) iv.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(target, DEFAULT_OBJECT_FACTORY,
            DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);

        // 여러 인터셉터를 거치면서 warp 되었을 가능성이 있기 때문에 아래 두 while문에서 
        // 원시 object를 추출한다. 
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }

        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }

        RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");

        // RowBounds를 파라미터로 넘긴 요청만 paging 처리한다.
        if (rowBounds == null || rowBounds.equals(RowBounds.DEFAULT)) {
            return iv.proceed();
        }

        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        String sql = boundSql.getSql().trim();

        // paging sql 만든다.
        int offset = rowBounds.getOffset();
        int limit = rowBounds.getLimit();

        if (offset < 0 || limit < 0) {
            throw new ApiException(ErrorCodes.INTERNAL_ERROR, "invalid RowBounds");
        }

        String pageSql = getPostgresqlPaginationSql(sql, offset, limit);

        metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
        // 물리적 pagination을 적용하였으니 Mybatis의 메모리 pagination은 더이상 
        // 필요하지 않기 때문에 rowBounds를 리셋한다.
        metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

        // 다음 interceptor로 넘긴다.
        return iv.proceed();
    }

    /**
     * sql를 paging sql로 바꾼다.
     *
     * @param sql
     * @param offset
     * @param limit
     * @return
     */
    private String getPostgresqlPaginationSql(String sql, int offset, int limit) {
        StringBuilder pagingSelect = new StringBuilder(sql);

        if (limit > 0) {
            pagingSelect.append(" LIMIT " + limit);
        }

        if (offset > 0) {
            pagingSelect.append(" OFFSET " + offset);
        }

        return pagingSelect.toString();
    }

    @Override
    public Object plugin(Object target) {
        // StatementHandler class 일때만 wrap 한다. 
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
