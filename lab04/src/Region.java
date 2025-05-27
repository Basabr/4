package mountainhuts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class {@code Region} represents the main facade
 * class for the mountain huts system.
 */
public class Region {

    private String name;
    private Map<String, Municipality> municipalities = new HashMap<>();
    private Map<String, MountainHut> mountainHuts = new HashMap<>();

    // کلاس کمکی برای بازه ارتفاعی
    private static class AltitudeRange {
        int min;
        int max;
        String label;

        AltitudeRange(int min, int max, String label) {
            this.min = min;
            this.max = max;
            this.label = label;
        }

        boolean contains(int altitude) {
            return altitude >= min && altitude <= max;
        }
    }

    private List<AltitudeRange> altitudeRanges = new ArrayList<>();

    public Region(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * تنظیم بازه‌های ارتفاعی با آرایه رشته‌ای مثل "0-1000", "1001-2000", ...
     */
    public void setAltitudeRanges(String... ranges) {
        altitudeRanges.clear();
        for (String range : ranges) {
            String[] parts = range.split("-");
            int min = Integer.parseInt(parts[0]);
            int max = parts[1].equalsIgnoreCase("INF") ? Integer.MAX_VALUE : Integer.parseInt(parts[1]);
            altitudeRanges.add(new AltitudeRange(min, max, range));
        }
    }

    /**
     * پیدا کردن بازه ارتفاعی که شامل ارتفاع داده شده باشد
     * @param altitude ارتفاع مورد نظر
     * @return رشته بازه یا "0-INF" اگر داخل هیچ بازه‌ای نبود
     */
    public String getAltitudeRange(Integer altitude) {
        if (altitude == null) return "0-INF";
        for (AltitudeRange range : altitudeRanges) {
            if (range.contains(altitude)) {
                return range.label;
            }
        }
        return "0-INF";
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
        return mountainHuts.computeIfAbsent(name, n -> new MountainHut(name, Optional.ofNullable(altitude), category, bedsNumber, municipality));
    }

    public static Region fromFile(String name, String file) {
        Region region = new Region(name);
        List<String> lines = readData(file);

        if (lines.isEmpty()) return region;

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

    // R1: متدهای زیر هنوز پیاده‌سازی نشده‌اند:
    public Map<String, Long> countMunicipalitiesPerProvince() {
        return null;
    }

    public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
        return null;
    }

    public Map<String, Long> countMountainHutsPerAltitudeRange() {
        return null;
    }

    public Map<String, Integer> totalBedsNumberPerProvince() {
        return null;
    }

    public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
        return null;
    }

    public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
        return null;
    }
}
