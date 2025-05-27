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

    // رنج‌های ارتفاعی ذخیره شده به صورت لیست از شی Range
    private List<Range> altitudeRanges = new ArrayList<>();

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

    // ایجاد یا بازیابی Municipality بر اساس نام (یکتا)
    public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
        return municipalities.computeIfAbsent(name, n -> new Municipality(name, province, altitude));
    }

    // ایجاد یا بازیابی MountainHut بدون ارتفاع (Optional.empty)
    public MountainHut createOrGetMountainHut(String name, String category,
                                              Integer bedsNumber, Municipality municipality) {
        return mountainHuts.computeIfAbsent(name, n -> new MountainHut(name, Optional.empty(), category, bedsNumber, municipality));
    }

    // ایجاد یا بازیابی MountainHut با ارتفاع مشخص (Optional.ofNullable)
    public MountainHut createOrGetMountainHut(String name, Integer altitude, String category,
                                              Integer bedsNumber, Municipality municipality) {
        return mountainHuts.computeIfAbsent(name, n -> new MountainHut(name, Optional.ofNullable(altitude), category, bedsNumber, municipality));
    }

    /**
     * R1 - تعریف رنج‌های ارتفاعی به صورت دینامیک
     * هر رشته ورودی در قالب "min-max" است.
     */
    public void setAltitudeRanges(String[] ranges) {
        altitudeRanges.clear();
        for (String r : ranges) {
            String[] parts = r.trim().split("-");
            if (parts.length != 2) continue;
            try {
                int min = Integer.parseInt(parts[0].trim());
                String maxStr = parts[1].trim();
                int max;
                if (maxStr.equalsIgnoreCase("INF") || maxStr.equalsIgnoreCase("INFINITY")) {
                    max = Integer.MAX_VALUE;
                } else {
                    max = Integer.parseInt(maxStr);
                }
                altitudeRanges.add(new Range(min, max, r.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    /**
     * R1 - برگرداندن رشته رنج ارتفاع برای ارتفاع داده شده
     * اگر ارتفاع در هیچ رنجی نبود، "0-INF" برگردانده می‌شود.
     */
    public String getAltitudeRange(Integer altitude) {
        if (altitude == null) return "0-INF";
        for (Range range : altitudeRanges) {
            if (range.includes(altitude)) {
                return range.getLabel();
            }
        }
        return "0-INF";
    }

    /**
     * R3 - ایجاد Region از فایل CSV
     */
    public static Region fromFile(String name, String file) {
        Region region = new Region(name);
        List<String> lines = readData(file);

        if (lines.isEmpty()) return region;

        lines.remove(0); // skip header
        for (String line : lines) {
            String[] fields = line.split(";");
            if (fields.length < 7) continue; // ignore incomplete lines

            try {
                String province = fields[0].trim();
                String municipalityName = fields[1].trim();
                Integer municipalityAltitude = fields[2].trim().isEmpty() ? null : Integer.parseInt(fields[2].trim());
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
            } catch (NumberFormatException e) {
                // خطا در تبدیل عدد - می‌توان لاگ گرفت یا رد کرد
                System.err.println("Error parsing line: " + line + " - " + e.getMessage());
            }
        }

        return region;
    }

    /**
     * خواندن داده‌ها از فایل به صورت لیست رشته‌ها
     */
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
     * کلاس داخلی برای نگهداری رنج ارتفاعی
     */
    private static class Range {
        private final int min;
        private final int max;
        private final String label;

        public Range(int min, int max, String label) {
            this.min = min;
            this.max = max;
            this.label = label;
        }

        public boolean includes(int value) {
            return value >= min && value <= max;
        }

        public String getLabel() {
            return label;
        }
    }
}
