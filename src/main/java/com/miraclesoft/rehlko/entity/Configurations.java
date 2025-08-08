package com.miraclesoft.rehlko.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("backend_configurations")
public class Configurations {

    @Id
    @Column("id")
    private Integer id;

    @Column("s3_bucket_access_key")
    private String s3_bucket_access_key;

    @Column("s3_bucket_sceret_key")
    private String s3_bucket_sceret_key;

    @Column("s3_bucket_name")
    private String s3_bucket_name;

    @Column("s3_bucket_region")
    private String s3_bucket_region;

    @Column("smtp_host_name")
    private String smtpHostName;

    @Column("smtp_from_mailid")
    private String smtpFromMailId;
    @Column("smtp_port")
    private String smtpPort;

    @Column("smtp_fromid_pwd")
    private String smtpFromidPwd;

    @Column("856_submit")
    private String submit856;

    @Column("810_submit")
    private String submit810;

    @Column("855_submit")
    private String submit855;

}
