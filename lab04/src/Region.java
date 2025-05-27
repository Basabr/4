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
        return mountainHuts.computeIfAbsent(name, n -> new MountainHut(name, Optional.empty(), category, bedsNumber, municipality));
    }

    public MountainHut createOrGetMountainHut(String name, Integer altitude, String category,
                                              Integer bedsNumber, Municipality municipality) {
        return mountainHuts.computeIfAbsent(name, n -> new MountainHut(name, Optional.ofNullable(altitude), category, bedsNumber, municipality));
    }

    /**
     * Static factory method to create a Region from CSV file
     */
    public static Region fromFile(String name, String file) {
        Region region = new Region(name);
        List<String> lines = readData(file);

        if (lines.isEmpty()) return region;

        lines.remove(0); // skip header
        for (String line : lines) {
            String[] fields = line.split(";");
            if (fields.length < 7) continue; // ignore incomplete lines

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

    /**
     * R4 Queries
     */

    // تعداد شهرداری‌ها به تفکیک استان
    public Map<String, Long> countMunicipalitiesPerProvince() {
        return municipalities.values().stream()
                .collect(Collectors.groupingBy(Municipality::getProvince, Collectors.counting()));
    }

    // تعداد پناهگاه‌های کوهستانی به تفکیک شهرداری و استان
    public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
        return mountainHuts.values().stream()
                .collect(Collectors.groupingBy(
                        mh -> mh.getMunicipality().getProvince(),
                        Collectors.groupingBy(
                                mh -> mh.getMunicipality().getName(),
                                Collectors.counting()
                        )
                ));
    }

    // تعداد پناهگاه‌ها به تفکیک بازه ارتفاعی (اگر ارتفاع پناهگاه موجود نبود، ارتفاع شهرداری در نظر گرفته می‌شود)
    public Map<String, Long> countMountainHutsPerAltitudeRange() {
        return mountainHuts.values().stream()
                .collect(Collectors.groupingBy(
                        mh -> {
                            Integer altitude = mh.getAltitude().orElse(mh.getMunicipality().getAltitude());
                            return getAltitudeRange(altitude);
                        },
                        Collectors.counting()
                ));
    }

    // مجموع تخت‌ها به تفکیک استان
    public Map<String, Integer> totalBedsNumberPerProvince() {
        return mountainHuts.values().stream()
                .collect(Collectors.groupingBy(
                        mh -> mh.getMunicipality().getProvince(),
                        Collectors.summingInt(MountainHut::getBedsNumber)
                ));
    }

    // بیشینه تخت‌ها در یک پناهگاه به تفکیک بازه ارتفاعی
    public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
        return mountainHuts.values().stream()
                .collect(Collectors.groupingBy(
                        mh -> {
                            Integer altitude = mh.getAltitude().orElse(mh.getMunicipality().getAltitude());
                            return getAltitudeRange(altitude);
                        },
                        Collectors.mapping(
                                MountainHut::getBedsNumber,
                                Collectors.maxBy(Integer::compareTo)
                        )
                ));
    }

    // نام شهرداری‌ها بر اساس تعداد پناهگاه‌ها (مرتب شده بر اساس حروف الفبا)
    public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
        Map<String, Long> hutsCountPerMunicipality = mountainHuts.values().stream()
                .collect(Collectors.groupingBy(
                        mh -> mh.getMunicipality().getName(),
                        Collectors.counting()
                ));

        Map<Long, List<String>> result = hutsCountPerMunicipality.entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.mapping(
                                Map.Entry::getKey,
                                Collectors.toList()
                        )
                ));

        result.values().forEach(Collections::sort);

        return result;
    }

    /**
     * متد کمکی برای تعیین بازه ارتفاعی (می‌توانید متناسب با نیاز خود تغییر دهید)
     */
    private String getAltitudeRange(Integer altitude) {
        if (altitude == null) return "Unknown";

        if (altitude < 1000) return "<1000";
        else if (altitude < 1500) return "1000-1499";
        else if (altitude < 2000) return "1500-1999";
        else return ">=2000";
    }

}
