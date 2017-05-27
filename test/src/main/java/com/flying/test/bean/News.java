package com.flying.test.bean;

import java.util.Arrays;

public class News {
    public static final int TYPE_PICTURE_TEXT = 1; // 图文类
    public static final int TYPE_PICTURES = 2; // 图片类
    public static final int TYPE_DUAN_ZI = 3; // 段子类
    public static final int TYPE_GIF = 4; // GIF类
    public static final int TYPE_VIDEO = 5; // 视频类
    public static final int TYPE_QUESTIONNAIRE = 6;// 问卷调查类

    public long contentId; // 新闻ID
    public String title;

    public int getType() {
        return type;
    }

    /**
     * @see #TYPE_PICTURE_TEXT ...
     * @see #TYPE_QUESTIONNAIRE
     */
    public int type; // 类型

    public String sourceDesc;// 内容源描述

    public long commentNum; // 评论数，默认为0

    public long praisenNum; // 点赞次数，默认为0

    public long originalReleaseTimestamp; // 原文发布时间(UTC时间戳)

    /**
     * 头图数组，如果没有头图，返回空数组。 有头图的情况： type=1时，为1或3张小图，218 * 180 type=2/3/4/5时，为1张大图，656 ＊ 400
     */
    public String[] headImages;

    public String detailUrl; // 详情URL，这个也可能是问卷调查的URL，广告的URL等等

    public String token; // 该条新闻的TOKEN

    public long publishedTime; // 该条新闻的在本系统的发布时间

    public String body; // 段子类文章的body，一段不包括图片的纯文字；其他情况为“”

    public int views; // 该文章的浏览量

    public int imageCount; // 图片的数量，type为2时有值

    @Override
    public String toString() {
        return "News{" + "contentId=" + contentId + ", title='" + title + '\'' + ", type=" + type + ", sourceDesc='"
                + sourceDesc + '\'' + ", commentNum=" + commentNum + ", praisenNum=" + praisenNum
                + ", originalReleaseTimestamp=" + originalReleaseTimestamp + ", headImages="
                + Arrays.toString(headImages) + ", detailUrl='" + detailUrl + '\'' + ", token='" + token + '\''
                + ", publishedTime=" + publishedTime + ", body='" + body + '\'' + ", views=" + views + ", imageCount="
                + imageCount + '}';
    }
}
