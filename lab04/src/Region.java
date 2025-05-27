package mountainhuts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class {@code Region} represents the main facade
 * class for the mountains hut system.
 */
public class Region {

    private String name;
    private Map<String, Municipality> municipalities = new HashMap<>();
    private Map<String, MountainHut> mountainHuts = new HashMap<>();

    public Region(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAltitudeRanges(String... ranges) {
        // R1: Not required yet
    }

    public String getAltitudeRange(Integer altitude) {
        return "0-INF"; // R1: Not required yet
    }

    public Collection<Municipality> getMunicipalities() {
        return Collections.unmodifiableCollection(municipalities.values());
    }

    public Collection<MountainHut> getMountainHuts() {
        return Collections.unmodifiableCollection(mountainHuts.values());
    }

    public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
        return municipalities.computeIfAbsent(name, n -> new Municipality(name, province, altitude));
    }

    public MountainHut createOrGetMountainHut(String name, String category,
                                              Integer bedsNumber, Municipality municipality) {
        return mountainHuts.computeIfAbsent(name, n -> new MountainHut(name, null, category, bedsNumber, municipality));
    }

    public MountainHut createOrGetMountainHut(String name, Integer altitude, String category,
                                              Integer bedsNumber, Municipality municipality) {
        return mountainHuts.computeIfAbsent(name, n -> new MountainHut(name, altitude, category, bedsNumber, municipality));
    }

    public static Region fromFile(String name, String file) {
        Region region = new Region(name);
        List<String> lines = readData(file);

        String header = lines.remove(0); // skip header
        for (String line : lines) {
            String[] fields = line.split(";");
            String province = fields[0].trim();
            String municipalityName = fields[1].trim();
            Integer municipalityAltitude = Integer.parseInt(fields[2].trim());
            String hutName = fields[3].trim();
            String altitudeStr = fields[4].trim();
            Integer altitude = altitudeStr.isEmpty() ? null : Integer.parseInt(altitudeStr);
            String category = fields[5].trim();
            Integer bedsNumber = Integer.parseInt(fields[6].trim());

            Municipality municipality = region.createOrGetMunicipality(municipalityName, province, municipalityAltitude);
            if (altitude == null) {
                region.createOrGetMountainHut(hutName, category, bedsNumber, municipality);
            } else {
                region.createOrGetMountainHut(hutName, altitude, category, bedsNumber, municipality);
            }
        }

        return region;
    }

    public static List<String> readData(String file) {
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            return in.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Long> countMunicipalitiesPerProvince() {
        return null; // R1: not required
    }

    public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
        return null; // R1: not required
    }

    public Map<String, Long> countMountainHutsPerAltitudeRange() {
        return null; // R1: not required
    }

    public Map<String, Integer> totalBedsNumberPerProvince() {
        return null; // R1: not required
    }

    public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
        return null; // R1: not required
    }

    public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
        return null; // R1: not required
    }
}
