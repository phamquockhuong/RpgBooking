package com.example.RpgBooking.seeder;

import com.example.RpgBooking.model.Category;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.model.User;
import com.example.RpgBooking.repository.CategoryRepository;
import com.example.RpgBooking.repository.RoomRepository;
import com.example.RpgBooking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoryRepository categoryRepository,
                      RoomRepository roomRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (userRepository.count() > 0) return;

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@gmail.com");

        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);

        User user = new User();
        user.setUsername("nguyenvana");
        user.setEmail("vana@gmail.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        System.out.println("Đã seed tài khoản test: admin/admin123 và nguyenvana/user123");
        
        if (categoryRepository.count() > 0) {
            System.out.println("Dữ liệu mẫu đã tồn tại. Bỏ qua bước Seeding.");
            return;
        }

        System.out.println("Đang khởi tạo dữ liệu mẫu (Seeding dữ liệu)...");

        Category horror = new Category();
        horror.setName("Kinh dị");

        Category puzzle = new Category();
        puzzle.setName("Giải đố & Trinh thám");

        Category action = new Category();
        action.setName("Hành động & Phiêu lưu");

        categoryRepository.saveAll(Arrays.asList(horror, puzzle, action));

        Room room1 = new Room();
        room1.setName("Phòng Ám Ảnh Khách Sạn");
        room1.setDuration(90);
        room1.setMinPlayers(2);
        room1.setMaxPlayers(5);
        room1.setPriceAdult(50000);
        room1.setPriceKid(75000);
        room1.setCategory(horror);
        room1.setDescription("Các nhà khoa học biển, hãy khám phá những bí mật của đại dương sâu thẳm trong chuyến thám hiểm bằng tàu ngầm KIDY-01 và góp phần bảo vệ hệ động thực vật biển tuyệt đẹp cùng môi trường sống của chúng.");
        room1.setActive(true);

        Room room2 = new Room();
        room2.setName("Ngục Tối Alcatraz");
        room2.setDuration(45);
        room2.setMinPlayers(3);
        room2.setMaxPlayers(8);
        room2.setPriceAdult(25000);
        room2.setPriceKid(35000);
        room2.setCategory(action);
        room2.setDescription("Các nhà khoa học biển, hãy khám phá những bí mật của đại dương sâu thẳm trong chuyến thám hiểm bằng tàu ngầm KIDY-01 và góp phần bảo vệ hệ động thực vật biển tuyệt đẹp cùng môi trường sống của chúng.");
        room2.setActive(true);

        Room room3 = new Room();
        room3.setName("Mật Thất Louvre");
        room3.setDuration(60);
        room3.setMinPlayers(1);
        room3.setMaxPlayers(4);
        room3.setPriceAdult(18000);
        room3.setPriceKid(25000);
        room3.setCategory(puzzle);
        room3.setDescription("Các nhà khoa học biển, hãy khám phá những bí mật của đại dương sâu thẳm trong chuyến thám hiểm bằng tàu ngầm KIDY-01 và góp phần bảo vệ hệ động thực vật biển tuyệt đẹp cùng môi trường sống của chúng.");
        room3.setActive(true);

        roomRepository.saveAll(Arrays.asList(room1, room2, room3));

        System.out.println("Khởi tạo dữ liệu mẫu thành công!");
    }
}
