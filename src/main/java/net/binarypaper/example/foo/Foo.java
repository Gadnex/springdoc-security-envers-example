package net.binarypaper.example.foo;

import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@NamedQueries({
    @NamedQuery(name = "Foo.findAll", query = "SELECT f FROM Foo AS f ORDER BY f.name")
})
@Audited
@Data
public class Foo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({List.class, All.class, Update.class})
    private Long id;

    @Version
    @JsonView({All.class, Update.class})
    private Integer version;

    @JsonView({Add.class, List.class, All.class, Update.class})
    private String name;

    @JsonView({Add.class, All.class, Update.class})
    private String description;

    public interface Add {
    }

    public interface Update {
    }

    public interface All {
    }

    public interface List {
    }

}
