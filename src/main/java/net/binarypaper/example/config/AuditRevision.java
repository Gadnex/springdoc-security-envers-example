/*
 * Copyright 2020 <a href="mailto:willy.gadney@binarypaper.net">Willy Gadney</a>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.binarypaper.example.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

// JPA annotations
@Entity
@Table(name = "AUDIT_REVISION")
// Envers annotations
@RevisionEntity(AuditRevisionListener.class)
// Jackson annotations
@JsonPropertyOrder({
    "rev",
    "revisionDate",
    "userName",
    "revisionType"
})
// Lombok annotations
@Data
@EqualsAndHashCode
@ToString
public class AuditRevision implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The revision number of the audit revision.
     * <p>
     * Used to uniquely identify the audit revision object in the database.
     *
     * @param rev The revision number of the revision
     * @return The revision number of the revision
     */
    // JPA annotations
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Envers annotations
    @RevisionNumber
    // Jackson annotations
    @JsonProperty("rev")
    @JsonView({AuditRevision.class})
    // OpenAPI annotations
    @Schema(
            description = "The revision number of the audit revision",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "1"
    )
    private Long rev;

    /**
     * The timestamp of when the database audit revision was created.
     *
     * @param revisionTimestamp The timestamp of the revision
     * @return The timestamp of the revision
     */
    // JPA annotations
    @Column(name = "REV_TIMESTAMP", nullable = false, updatable = false)
    // Envers annotations
    @RevisionTimestamp
    // Jackson annotations
    @JsonIgnore
    private long revisionTimestamp;

    /**
     * The user name of the user that created the database audit revision.
     *
     * @param userName The user name of the user that made the revision
     * @return The timestamp of the revision
     */
    // JPA annotations
    @Column(name = "USER_NAME", nullable = false, updatable = false)
    // Jackson annotations
    @JsonProperty("user")
    @JsonView({AuditRevision.class})
    // OpenAPI annotations
    @Schema(
            description = "The user name of the user that created the database audit revision",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "username"
    )
    private String username;

    /**
     * The type of revision, which would be either ADD, MOD or DEL.
     * <p>
     * This field is not persisted to the revision table of the database, but
     * rather in the audit table of the specific entity. Therefore the field is
     * marked as @Transient.
     *
     * @param revisionType The revision type
     * @return The revision type
     */
    // JPA annotations
    @Transient
    // Jackson annotations
    @JsonProperty("type")
    @JsonView({AuditRevision.class})
    // OpenAPI annotations
    @Schema(
            description = "The type of revision",
            accessMode = Schema.AccessMode.READ_ONLY,
            allowableValues = "ADD, MOD, DEL",
            example = "ADD"
    )
    private String revisionType;

    /**
     * Return the date and time of revision based on the revisionTimestamp
     *
     * @return The date and time of revision
     */
    // Jackson annotations
    @JsonProperty("date")
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z", timezone = "GMT+02:00")
    @JsonView({AuditRevision.class})
    // OpenAPI annotations
    @Schema(
            description = "The date and time of revision",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "2020-02-07 10:47:39 GMT+02:00"
    )
    public Date getRevisionDate() {
        return new Date(revisionTimestamp);
    }

    public void setRevisionDate(Date revisionDate) {
        revisionTimestamp = revisionDate.getTime();
    }
}
