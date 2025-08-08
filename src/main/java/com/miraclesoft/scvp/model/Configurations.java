package com.miraclesoft.scvp.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class User.
 *
 * @author 
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Configurations extends SortingAndPagination {
    private Long id;
    private String globalImg;
    private String globalColor;
    private String submit_856;
    private String environment;
    private String submit_810;
    private String submit_855;
    private String s3BucketAccessKey;
    private String s3BucketSecretKey;
    private String bpidLink;   
    private String submit_997;
    private String recieve_997;
    private String createdBy;
    private String modifiedBy;
    private String webFormsTutorialLink;;
    private String mscvpTutorialLink;
    private String footer;
    private String smtpHostName;
    private String smtpFromMailId;
    private String smtpPort;
    private String smtpFromidPwd; 
    private String b2bReprocessUrl; 
    private String b2bReprocessSfgUrl; 
    private String s3BbucketName; 
    private String s3BbucketRegion; 
   
}
