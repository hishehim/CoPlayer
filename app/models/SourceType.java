package models;

import com.avaje.ebean.Model;
import statics.CONST;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by linmh on 3/9/2016.
 */
/**
 * Used to indicate the type of source the embedded link should be
 * @see CONST for list of acceptable sources
 * */
@Entity
public class SourceType extends Model {

    @Id
    long id;

    @Constraints.Required
    @Column(unique = true)
    private String sourceType;

    private SourceType() {}

    /**
     * The default and the only constructor for SourceType.
     * Takes the enum type CONST.SOURCE to obtain the string presentation of the source type
     * @param type indicates the embedded link's host source
     * */
    public SourceType(@Nonnull CONST.SOURCE type) {
        sourceType = type.toString();
    }

    public String getSourceType() {
        return sourceType;
    }

    public String toString() {
        return sourceType;
    }
}