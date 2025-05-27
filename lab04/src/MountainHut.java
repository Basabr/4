package mountainhuts;

import java.util.Optional;

/**
 * Represents a mountain hut
 * 
 * It includes a name, optional altitude, category,
 * number of beds and location municipality.
 */
public class MountainHut {

    private final String name;
    private final Optional<Integer> altitude;
    private String category;
    private Integer bedsNumber;
    private Municipality municipality;

    /**
     * Constructor with altitude as Optional<Integer>.
     * Null value for altitude will be converted to Optional.empty().
     * 
     * @param name         the name of the hut
     * @param altitude     optional altitude of the hut
     * @param category     category of the hut
     * @param bedsNumber   number of beds
     * @param municipality municipality where the hut is located
     */
    public MountainHut(String name, Optional<Integer> altitude, String category,
                       Integer bedsNumber, Municipality municipality) {
        this.name = name;
        this.altitude = altitude != null ? altitude : Optional.empty();
        this.category = category;
        this.bedsNumber = bedsNumber;
        this.municipality = municipality;
    }

    /**
     * Alternative constructor with altitude as Integer (nullable).
     * Converts altitude to Optional internally.
     * 
     * @param name         the name of the hut
     * @param altitude     altitude of the hut or null if unknown
     * @param category     category of the hut
     * @param bedsNumber   number of beds
     * @param municipality municipality where the hut is located
     */
    public MountainHut(String name, Integer altitude, String category,
                       Integer bedsNumber, Municipality municipality) {
        this(name, Optional.ofNullable(altitude), category, bedsNumber, municipality);
    }

    public String getName() {
        return name;
    }

    public Optional<Integer> getAltitude() {
        return altitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getBedsNumber() {
        return bedsNumber;
    }

    public void setBedsNumber(Integer bedsNumber) {
        this.bedsNumber = bedsNumber;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }
}
