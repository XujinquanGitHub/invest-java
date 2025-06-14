package com.example.invest.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GuornStrategy {
    private String id;
    private String name;
    private String description;
    private String creator;
    private Date createTime;
    private Date updateTime;
    private String status;
    private String type;
    private String tags;
    
    // 策略指标
    private BigDecimal performance;
    private BigDecimal risk;
    private BigDecimal sharpe;
    private BigDecimal maxDrawdown;
    private BigDecimal annualReturn;
    private BigDecimal totalReturn;
    
    // 统计信息
    private Integer followers;
    private Integer views;
    private Integer comments;
    private Integer likes;
    private Integer shares;
    
    // 状态标记
    private Boolean isPublic;
    private Boolean isFollowed;
    private Boolean isLiked;
    private Boolean isShared;
    private Boolean isCommented;
    private Boolean isViewed;
    private Boolean isCreator;
    private Boolean isAdmin;
    private Boolean isVerified;
    private Boolean isPremium;
    private Boolean isRecommended;
    private Boolean isHot;
    private Boolean isNew;
    private Boolean isTop;
    private Boolean isFeatured;
    private Boolean isDeleted;
    private Boolean isBlocked;
    private Boolean isReported;
    private Boolean isHidden;
    private Boolean isArchived;
    private Boolean isDraft;
    private Boolean isPending;
    private Boolean isRejected;
    private Boolean isApproved;
    private Boolean isPublished;
    private Boolean isUnpublished;
    private Boolean isScheduled;
    private Boolean isExpired;
    private Boolean isActive;
    private Boolean isEnabled;
    private Boolean isLocked;
} 