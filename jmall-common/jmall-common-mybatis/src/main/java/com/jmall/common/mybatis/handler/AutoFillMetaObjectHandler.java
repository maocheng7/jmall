package com.jmall.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jmall.common.core.constant.CommonConstants;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * <p>
 * 自动填充 createTime、updateTime、deleted 字段，无需在业务代码中手动赋值。
 * </p>
 *
 * @author jmall
 */
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 插入时填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 插入时填充逻辑删除标识（未删除）
        this.strictInsertFill(metaObject, "deleted", Integer.class, CommonConstants.NOT_DELETED);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
