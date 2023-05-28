package com.af.common.moudules.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author liaohuiquan
 * @since 2020-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmapArea implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String pId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
