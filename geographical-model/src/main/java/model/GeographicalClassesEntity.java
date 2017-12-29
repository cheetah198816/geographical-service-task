package model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Created by chetan on 22.12.2017.
 */
@Entity
@Table(name = "geographical_classes")
@SequenceGenerator(name = "seq_geo_classes", sequenceName = "seq_geo_classes")
@Data
@EqualsAndHashCode
public class GeographicalClassesEntity {

    @Id
    @GeneratedValue(generator = "seq_geo_classes")
    private Long id;

    private String code;

    private String name;

    @ManyToOne
    private SectionEntity section;
}
