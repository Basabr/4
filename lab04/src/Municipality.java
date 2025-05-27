package mountainhuts;

/**
 * Class representing a municipality that hosts a mountain hut.
 * It is a data class with getters and setters for name, province, and altitude.
 */
public class Municipality {

    private final String name;
    private String province;
    private Integer altitude;

    public Municipality(String name, String province, Integer altitude) {
        this.name = name;
        this.province = province;
        this.altitude = altitude;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }
}
