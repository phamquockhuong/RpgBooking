package com.example.RpgBooking.seeder;

import com.example.RpgBooking.model.Category;
import com.example.RpgBooking.model.Faq;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.model.User;
import com.example.RpgBooking.repository.CategoryRepository;
import com.example.RpgBooking.repository.RoomRepository;
import com.example.RpgBooking.repository.UserRepository;
import com.example.RpgBooking.repository.FaqRepository;
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
    private final FaqRepository faqRepository;

    public DataSeeder(CategoryRepository categoryRepository,
                      RoomRepository roomRepository,
                      UserRepository userRepository,
                      FaqRepository faqRepository,
                      PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.faqRepository = faqRepository;
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
        puzzle.setName("Giải đố");

        Category adventure = new Category();
        adventure.setName("Trinh thám");

        Category action = new Category();
        action.setName("Hành động");

        Category mystery = new Category();
        mystery.setName("Phiêu lưu");

        Category scifi = new Category();
        scifi.setName("Khoa học viễn tưởng");

        categoryRepository.saveAll(Arrays.asList(horror, puzzle, action, adventure, mystery, scifi));

        Room room1 = new Room();
        room1.setName("Phòng Ám Ảnh Khách Sạn");
        room1.setDescription(
                "Bạn tỉnh dậy trong một khách sạn bỏ hoang giữa đêm mưa bão. "
                        + "Không có ai xung quanh, chỉ còn những tiếng bước chân vang lên từ hành lang tối. "
                        + "Mỗi căn phòng đều ẩn chứa một bí mật kinh hoàng, và bạn phải tìm cách thoát ra trước khi khách sạn ‘đóng cửa’ mãi mãi."
        );
        room1.setDuration(90);
        room1.setMinPlayers(2);
        room1.setMaxPlayers(5);
        room1.setPriceAdult(50000);
        room1.setPriceKid(75000);
        room1.setImageUrl("/uploads/room1.png");
        room1.setCategory(horror);
        room1.setActive(true);

        Room room2 = new Room();
        room2.setName("Ngục Tối Alcatraz");
        room2.setDescription(
                "Bạn bị giam giữ trong nhà tù Alcatraz – nơi được mệnh danh là không thể trốn thoát. "
                        + "Hệ thống an ninh dày đặc, các cai ngục luôn theo dõi từng bước di chuyển của bạn. "
                        + "Chỉ có trí thông minh và sự phối hợp nhóm mới giúp bạn mở được cánh cửa tự do."
        );
        room2.setDuration(60);
        room2.setMinPlayers(3);
        room2.setMaxPlayers(8);
        room2.setPriceAdult(25000);
        room2.setPriceKid(35000);
        room2.setImageUrl("/uploads/room2.png");
        room2.setCategory(action);
        room2.setActive(true);

        Room room3 = new Room();
        room3.setName("Mật Thất Louvre");
        room3.setDescription(
                "Một cổ vật bí ẩn vừa biến mất khỏi bảo tàng Louvre. "
                        + "Bạn là đội điều tra được cử đến hiện trường, nơi mọi dấu vết đều dẫn đến những bí mật cổ xưa. "
                        + "Liệu bạn có giải mã được lời nguyền trước khi nó thức tỉnh?"
        );
        room3.setDuration(60);
        room3.setMinPlayers(1);
        room3.setMaxPlayers(4);
        room3.setPriceAdult(18000);
        room3.setPriceKid(25000);
        room3.setImageUrl("/uploads/room3.png");
        room3.setCategory(puzzle);
        room3.setActive(true);

        Room room4 = new Room();
        room4.setName("Thí Nghiệm Zombie");
        room4.setDescription(
                "Một thí nghiệm sinh học thất bại đã khiến virus zombie lan ra toàn bộ phòng nghiên cứu. "
                        + "Hệ thống bị khóa, thời gian cạn dần và những tiếng gào thét vang lên trong bóng tối. "
                        + "Bạn phải tìm huyết thanh trước khi trở thành nạn nhân tiếp theo."
        );
        room4.setDuration(75);
        room4.setMinPlayers(2);
        room4.setMaxPlayers(6);
        room4.setPriceAdult(45000);
        room4.setPriceKid(60000);
        room4.setImageUrl("/uploads/room4.png");
        room4.setCategory(horror);
        room4.setActive(true);

        Room room5 = new Room();
        room5.setName("Kho Báu Pharaoh");
        room5.setDescription(
                "Bên trong kim tự tháp cổ đại Ai Cập, một kho báu bị nguyền rủa đang chờ được khám phá. "
                        + "Những cạm bẫy cổ xưa, xác ướp và lời nguyền bảo vệ kho báu hàng nghìn năm tuổi. "
                        + "Bạn có đủ can đảm để vượt qua tất cả và trở về an toàn?"
        );
        room5.setDuration(60);
        room5.setMinPlayers(2);
        room5.setMaxPlayers(5);
        room5.setPriceAdult(40000);
        room5.setPriceKid(55000);
        room5.setImageUrl("/uploads/room5.png");
        room5.setCategory(adventure);
        room5.setActive(true);

        Room room6 = new Room();
        room6.setName("Vụ Án Biệt Thự");
        room6.setDescription(
                "Một vụ án mạng bí ẩn xảy ra trong biệt thự của một gia tộc giàu có. "
                        + "Không ai được phép rời đi cho đến khi sự thật được phơi bày. "
                        + "Mỗi căn phòng đều chứa một mảnh ghép của sự thật – nhưng cũng đầy dối trá."
        );
        room6.setDuration(90);
        room6.setMinPlayers(1);
        room6.setMaxPlayers(6);
        room6.setPriceAdult(55000);
        room6.setPriceKid(70000);
        room6.setImageUrl("/uploads/room6.png");
        room6.setCategory(mystery);
        room6.setActive(true);

        Room room7 = new Room();
        room7.setName("Trạm Vũ Trụ Mất Tích");
        room7.setDescription(
                "Trạm vũ trụ ngoài quỹ đạo Trái Đất gặp sự cố nghiêm trọng. "
                        + "Hệ thống oxy đang cạn dần và liên lạc với Trái Đất bị gián đoạn hoàn toàn. "
                        + "Bạn phải khôi phục hệ thống trước khi trạm rơi vào trạng thái vô vọng."
        );
        room7.setDuration(75);
        room7.setMinPlayers(2);
        room7.setMaxPlayers(5);
        room7.setPriceAdult(60000);
        room7.setPriceKid(85000);
        room7.setImageUrl("/uploads/room7.png");
        room7.setCategory(scifi);
        room7.setActive(true);

        Room room8 = new Room();
        room8.setName("Rừng Ma Ám");
        room8.setDescription(
                "Bạn bị lạc trong khu rừng bị nguyền rủa, nơi thời gian như ngừng trôi. "
                        + "Những âm thanh kỳ lạ vang lên từ sâu trong bóng tối và ánh sáng dần biến mất. "
                        + "Mỗi bước đi có thể là bước cuối cùng nếu bạn không tìm ra lối thoát."
        );
        room8.setDuration(60);
        room8.setMinPlayers(2);
        room8.setMaxPlayers(4);
        room8.setPriceAdult(30000);
        room8.setPriceKid(45000);
        room8.setImageUrl("/uploads/room8.png");
        room8.setCategory(horror);
        room8.setActive(true);

        roomRepository.saveAll(Arrays.asList(
                room1, room2, room3, room4,
                room5, room6, room7, room8
        ));

        if (faqRepository.count() > 0) {
            return;
        }

        Faq faq1 = new Faq();
        faq1.setQuestion("Độ tuổi nào phù hợp để tham gia?");
        faq1.setAnswer("Người chơi dưới 16 tuổi cần có người giám hộ đi kèm. Trò chơi đòi hỏi khả năng tư duy logic nên khuyến khích người chơi từ 12 tuổi trở lên.");
        faq1.setLevel(1);
        faq1.setActive(true);

        Faq faq2 = new Faq();
        faq2.setQuestion("Tôi cần chuẩn bị gì trước khi đến?");
        faq2.setAnswer("Bạn nên có mặt trước giờ bắt đầu 15 phút để được nghe hướng dẫn và gửi đồ cá nhân vào tủ bảo quản.");
        faq2.setLevel(2);
        faq2.setActive(true);

        Faq faq3 = new Faq();
        faq3.setQuestion("Làm sao để tôi thay đổi lịch đã đặt?");
        faq3.setAnswer("Bạn có thể thay đổi lịch trước ít nhất 24 giờ. Vui lòng liên hệ hotline hoặc email để được hỗ trợ.");
        faq3.setLevel(3);
        faq3.setActive(true);

        Faq faq4 = new Faq();
        faq4.setQuestion("Phương thức thanh toán nào được chấp nhận?");
        faq4.setAnswer("Chúng tôi chấp nhận chuyển khoản ngân hàng, Momo, VNPay hoặc thanh toán trực tiếp tại quầy.");
        faq4.setLevel(4);
        faq4.setActive(true);

        Faq faq5 = new Faq();
        faq5.setQuestion("Trò chơi có quá đáng sợ hoặc gây ngột ngạt không?");
        faq5.setAnswer("Các phòng đều được trang bị hệ thống thông gió và nút khẩn cấp để đảm bảo an toàn cho người chơi.");
        faq5.setLevel(5);
        faq5.setActive(true);

        Faq faq6 = new Faq();
        faq6.setQuestion("Tôi có thể chơi ghép cùng nhóm khác không?");
        faq6.setAnswer("Một số sự kiện đặc biệt hỗ trợ ghép nhóm theo yêu cầu. Vui lòng liên hệ để được tư vấn.");
        faq6.setLevel(6);
        faq6.setActive(true);

        faqRepository.saveAll(Arrays.asList(
                faq1,
                faq2,
                faq3,
                faq4,
                faq5,
                faq6
        ));

        System.out.println("Khởi tạo dữ liệu mẫu thành công!");
    }
}
