package com.example.cyclexbe.config;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BikeListingRepository bikeListingRepository;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder,
            BikeListingRepository bikeListingRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bikeListingRepository = bikeListingRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Seed SHIPPER account
        userRepository.findByEmail("shipper@cyclex.com").ifPresentOrElse(
                existing -> {
                    boolean dirty = false;
                    if (!"ACTIVE".equals(existing.getStatus())) {
                        existing.setStatus("ACTIVE");
                        dirty = true;
                    }
                    if (existing.getRole() != Role.SHIPPER) {
                        existing.setRole(Role.SHIPPER);
                        dirty = true;
                    }
                    if (!existing.isVerify()) {
                        existing.setVerify(true);
                        dirty = true;
                    }
                    if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                        existing.setPhone("09847588237");
                        dirty = true;
                    }
                    if (existing.getCccd() == null || existing.getCccd().isBlank()) {
                        existing.setCccd("072998003394");
                        dirty = true;
                    }
                    if (dirty) {
                        userRepository.save(existing);
                    }
                },
                () -> {
                    User shipper = new User();
                    shipper.setEmail("shipper@cyclex.com");
                    shipper.setPasswordHash(passwordEncoder.encode("123456"));
                    shipper.setRole(Role.SHIPPER);
                    shipper.setFullName("Shipper");
                    shipper.setVerify(true);
                    shipper.setStatus("ACTIVE");
                    shipper.setPhone("09847588237");
                    shipper.setCccd("072998003394");
                    userRepository.save(shipper);
                });
        userRepository.findByEmail("admin@cyclex.com").ifPresentOrElse(
                existing -> {
                    // Ensure the admin account is always active and has the correct role
                    boolean dirty = false;
                    if (!"ACTIVE".equals(existing.getStatus())) {
                        existing.setStatus("ACTIVE");
                        dirty = true;
                    }
                    if (existing.getRole() != Role.ADMIN) {
                        existing.setRole(Role.ADMIN);
                        dirty = true;
                    }
                    if (!existing.isVerify()) {
                        existing.setVerify(true);
                        dirty = true;
                    }
                    if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                        existing.setPhone("09847588237");
                        dirty = true;
                    }
                    if (existing.getCccd() == null || existing.getCccd().isBlank()) {
                        existing.setCccd("072998003394");
                        dirty = true;
                    }
                    if (dirty) {
                        userRepository.save(existing);
                    }
                },
                () -> {
                    User admin = new User();
                    admin.setEmail("admin@cyclex.com");
                    admin.setPasswordHash(passwordEncoder.encode("admin123"));
                    admin.setRole(Role.ADMIN);
                    admin.setFullName("Admin");
                    admin.setVerify(true);
                    admin.setStatus("ACTIVE");
                    admin.setPhone("09847588237");
                    admin.setCccd("072998003394");
                    userRepository.save(admin);
                });

        // Seed BUYER account
        userRepository.findByEmail("buyer@cyclex.com").ifPresentOrElse(
                existing -> {
                    boolean dirty = false;
                    if (!"ACTIVE".equals(existing.getStatus())) {
                        existing.setStatus("ACTIVE");
                        dirty = true;
                    }
                    if (existing.getRole() != Role.BUYER) {
                        existing.setRole(Role.BUYER);
                        dirty = true;
                    }
                    if (!existing.isVerify()) {
                        existing.setVerify(true);
                        dirty = true;
                    }
                    if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                        existing.setPhone("09847588237");
                        dirty = true;
                    }
                    if (existing.getCccd() == null || existing.getCccd().isBlank()) {
                        existing.setCccd("072998003394");
                        dirty = true;
                    }
                    if (dirty) {
                        userRepository.save(existing);
                    }
                },
                () -> {
                    User buyer = new User();
                    buyer.setEmail("buyer@cyclex.com");
                    buyer.setPasswordHash(passwordEncoder.encode("123456"));
                    buyer.setRole(Role.BUYER);
                    buyer.setFullName("Buyer");
                    buyer.setVerify(true);
                    buyer.setStatus("ACTIVE");
                    buyer.setPhone("09847588237");
                    buyer.setCccd("072998003394");
                    userRepository.save(buyer);
                });

        // Seed INSPECTOR account
        userRepository.findByEmail("inspector@cyclex.com").ifPresentOrElse(
                existing -> {
                    boolean dirty = false;
                    if (!"ACTIVE".equals(existing.getStatus())) {
                        existing.setStatus("ACTIVE");
                        dirty = true;
                    }
                    if (existing.getRole() != Role.INSPECTOR) {
                        existing.setRole(Role.INSPECTOR);
                        dirty = true;
                    }
                    if (!existing.isVerify()) {
                        existing.setVerify(true);
                        dirty = true;
                    }
                    if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                        existing.setPhone("09847588237");
                        dirty = true;
                    }
                    if (existing.getCccd() == null || existing.getCccd().isBlank()) {
                        existing.setCccd("072998003394");
                        dirty = true;
                    }
                    if (dirty) {
                        userRepository.save(existing);
                    }
                },
                () -> {
                    User inspector = new User();
                    inspector.setEmail("inspector@cyclex.com");
                    inspector.setPasswordHash(passwordEncoder.encode("123456"));
                    inspector.setRole(Role.INSPECTOR);
                    inspector.setFullName("Inspector");
                    inspector.setVerify(true);
                    inspector.setStatus("ACTIVE");
                    inspector.setPhone("09847588237");
                    inspector.setCccd("072998003394");
                    userRepository.save(inspector);
                });

        // Seed SELLER account
        User seller = userRepository.findByEmail("seller@cyclex.com").orElseGet(() -> {
            User s = new User();
            s.setEmail("seller@cyclex.com");
            s.setPasswordHash(passwordEncoder.encode("123456"));
            s.setRole(Role.SELLER);
            s.setFullName("Seller");
            s.setVerify(true);
            s.setStatus("ACTIVE");
            s.setPhone("09847588237");
            s.setCccd("072998003394");
            return userRepository.save(s);
        });
        // Ensure seller is active
        if (!"ACTIVE".equals(seller.getStatus()) || seller.getRole() != Role.SELLER || !seller.isVerify()) {
            seller.setStatus("ACTIVE");
            seller.setRole(Role.SELLER);
            seller.setVerify(true);
            if (seller.getPhone() == null || seller.getPhone().isBlank())
                seller.setPhone("09847588237");
            if (seller.getCccd() == null || seller.getCccd().isBlank())
                seller.setCccd("072998003394");
            userRepository.save(seller);
        }

        // Seed 15 DRAFT listings for seller
        long draftCount = bikeListingRepository.countBySellerAndStatus(seller, BikeListingStatus.DRAFT);
        if (draftCount < 15) {
            String[][] bikes = {
                    { "Xe đạp địa hình Giant Talon 3", "Mountain", "Giant", "Talon 3", "2023", "Mới 95%", "6 tháng",
                            "Nâng cấp xe mới", "8500000", "Hà Nội", "123 Cầu Giấy, Hà Nội" },
                    { "Trek Marlin 7 - Đi nhẹ", "Mountain", "Trek", "Marlin 7", "2022", "Mới 90%", "1 năm",
                            "Ít sử dụng", "12000000", "TP Hồ Chí Minh", "45 Nguyễn Huệ, Quận 1" },
                    { "Xe đạp đua Merida Scultura 400", "Road", "Merida", "Scultura 400", "2023", "Mới 98%", "3 tháng",
                            "Chuyển sang leo núi", "18500000", "Đà Nẵng", "78 Trần Phú, Đà Nẵng" },
                    { "Specialized Allez Sprint", "Road", "Specialized", "Allez Sprint", "2021", "Mới 85%", "2 năm",
                            "Cần bán gấp", "22000000", "Hà Nội", "56 Hoàng Hoa Thám, Ba Đình" },
                    { "Xe đạp gấp Dahon Mu D9", "Folding", "Dahon", "Mu D9", "2024", "Mới 99%", "1 tháng",
                            "Mua nhầm size", "7500000", "TP Hồ Chí Minh", "12 Lý Thường Kiệt, Quận 10" },
                    { "Cannondale Trail 5 màu đen", "Mountain", "Cannondale", "Trail 5", "2022", "Mới 88%", "1.5 năm",
                            "Dọn nhà", "14000000", "Hải Phòng", "99 Lạch Tray, Hải Phòng" },
                    { "Xe đạp thể thao Java Siluro 3", "Road", "Java", "Siluro 3", "2023", "Mới 92%", "8 tháng",
                            "Đổi xe", "9800000", "Cần Thơ", "34 Nguyễn Trãi, Ninh Kiều" },
                    { "BMX Mongoose Legion L100", "BMX", "Mongoose", "Legion L100", "2023", "Mới 85%", "10 tháng",
                            "Chuyển môn", "5200000", "Bình Dương", "67 Đại lộ Bình Dương" },
                    { "Xe đạp điện trợ lực Giant Escape", "Electric", "Giant", "Escape E+", "2024", "Mới 97%",
                            "2 tháng", "Thừa xe", "25000000", "Hà Nội", "88 Giải Phóng, Hai Bà Trưng" },
                    { "Scott Scale 970 xám bạc", "Mountain", "Scott", "Scale 970", "2022", "Mới 80%", "2 năm",
                            "Cần tiền", "11000000", "TP Hồ Chí Minh", "202 Cách Mạng Tháng 8, Quận 3" },
                    { "Xe touring Surly Long Haul Trucker", "Touring", "Surly", "Long Haul Trucker", "2021", "Mới 82%",
                            "3 năm", "Đi du lịch xong", "16500000", "Đà Nẵng", "15 Bạch Đằng, Đà Nẵng" },
                    { "Trinx Climber 2.1 khung carbon", "Road", "Trinx", "Climber 2.1", "2023", "Mới 93%", "5 tháng",
                            "Upgrade khung mới", "7200000", "Nghệ An", "22 Quang Trung, Vinh" },
                    { "Xe đạp trẻ em Royalbaby Hero 20", "Kids", "Royalbaby", "Hero 20", "2024", "Mới 99%", "1 tháng",
                            "Con lớn nhanh", "3500000", "Hà Nội", "45 Nguyễn Chí Thanh, Đống Đa" },
                    { "Fixed gear Tsunami SNM100", "Fixed Gear", "Tsunami", "SNM100", "2023", "Mới 90%", "7 tháng",
                            "Chuyển sang Road", "4800000", "TP Hồ Chí Minh", "100 Nguyễn Đình Chiểu, Quận 3" },
                    { "Xe đạp đường phố Java Vento", "Hybrid", "Java", "Vento", "2024", "Mới 96%", "2 tháng",
                            "Không hợp phong cách", "6800000", "Huế", "33 Lê Lợi, TP Huế" },
            };
            int toCreate = 15 - (int) draftCount;
            for (int i = 0; i < toCreate && i < bikes.length; i++) {
                String[] b = bikes[(int) draftCount + i < bikes.length ? (int) draftCount + i : i];
                BikeListing listing = new BikeListing();
                listing.setSeller(seller);
                listing.setTitle(b[0]);
                listing.setBikeType(b[1]);
                listing.setBrand(b[2]);
                listing.setModel(b[3]);
                listing.setManufactureYear(Integer.parseInt(b[4]));
                listing.setCondition(b[5]);
                listing.setUsageTime(b[6]);
                listing.setReasonForSale(b[7]);
                listing.setPrice(new BigDecimal(b[8]));
                listing.setLocationCity(b[9]);
                listing.setPickupAddress(b[10]);
                listing.setDescription("Xe còn rất mới, đầy đủ phụ kiện. Lý do bán: " + b[7].toLowerCase()
                        + ". Liên hệ để xem xe trực tiếp.");
                listing.setStatus(BikeListingStatus.DRAFT);
                bikeListingRepository.save(listing);
            }
        }
    }
}
