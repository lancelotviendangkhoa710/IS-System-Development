# CHƯƠNG 5: SẢN PHẨM PHẦN MỀM

## 5.1. Giới thiệu sản phẩm

### 5.1.1. Tên gọi và mục tiêu của sản phẩm
Ứng dụng phần mềm được xây dựng và phát triển dưới tên gọi chính thức là **Hệ thống Quản lý Tiệm bánh Chuyên nghiệp** (sau đây gọi tắt là **BMS** - *Bakery Management System*). Dự án này được hình thành nhằm đáp ứng nhu cầu cấp thiết về việc tối ưu hóa quy trình quản lý và vận hành trong mô hình tiệm bánh hiện đại – một loại hình kinh doanh đặc thù kết hợp chặt chẽ giữa hai hoạt động sản xuất tức thời (tại khu vực bếp bánh) và thương mại dịch vụ (bán lẻ trực tiếp tại quầy kết hợp sản xuất theo đơn đặt hàng tùy chỉnh). BMS hướng đến việc số hóa toàn diện các luồng công việc phức tạp, nâng cao tính minh bạch tài chính và kiểm soát chất lượng chuỗi cung ứng nguyên liệu theo thời gian thực.

Trên cơ sở khảo sát thực tế, hệ thống BMS tập trung giải quyết triệt để các bài toán cốt lõi trong quản lý và vận hành tiệm bánh:
*   **Hạn chế tối đa thất thoát tài chính nhờ cơ chế kiểm soát nội bộ:** Hệ thống giải quyết vấn đề chênh lệch doanh thu và dòng tiền mặt tại quầy thông qua quy trình "đối soát mù" (blind-close cashier shift) khi kết thúc ca làm việc. Thu ngân buộc phải tự đếm và khai báo lượng tiền mặt thực tế một cách độc lập trước khi hệ thống thực hiện đối chiếu tự động với số liệu lý thuyết ghi nhận dưới cơ sở dữ liệu, từ đó ngăn ngừa hoàn toàn các gian lận hoặc sai sót chủ quan trong quá trình bàn giao ca.
*   **Tối ưu hóa quản lý hàng tồn kho cận hạn và giảm thiểu lãng phí vật tư:** Đối với nguyên liệu ngành bánh có thời hạn sử dụng tương đối ngắn, hệ thống tích hợp giải pháp kho thông minh vận hành theo nguyên lý FEFO (*First Expired, First Out* - Hết hạn trước, Xuất trước). Việc tự động xác định và ưu tiên xuất dùng các lô hàng có hạn sử dụng gần nhất giúp tiệm bánh duy trì chất lượng thành phẩm đồng đều và hạn chế tối đa tỷ lệ hủy hao nguyên vật liệu.
*   **Điều phối năng lực sản xuất của bếp bánh nhằm ngăn ngừa hiện tượng quá tải:** Hệ thống giải quyết triệt để sự mất cân đối giữa lượng đơn đặt hàng tùy chỉnh và năng lực vận hành thực tế của xưởng sản xuất thông qua thuật toán tự động tính toán thời gian chuẩn bị và kiểm soát tải trọng, ngăn chặn tức thời các đơn hàng vượt quá công suất tối đa của bếp tại thời điểm tương ứng.
*   **Giảm thiểu rủi ro kinh doanh đối với đơn hàng bánh đặt trước:** Áp dụng nghiêm ngặt "Quy tắc 50%" đặt cọc tối thiểu đối với bánh thiết kế tùy chỉnh dưới tầng nghiệp vụ dữ liệu, thiết lập một chốt chặn tài chính vững chắc giúp bảo vệ doanh nghiệp trước các rủi ro phát sinh từ việc khách hàng hủy đơn đột ngột.

### 5.1.2. Đối tượng sử dụng và phân quyền hệ thống (Actors)
Hệ thống BMS thực hiện phân cấp quyền hạn chặt chẽ dựa trên mô hình Kiểm soát truy cập dựa trên vai trò (Role-Based Access Control - RBAC). Cơ chế này phân chia người dùng trực tiếp tương tác với phần mềm thành bốn nhóm đối tượng (Actors) với các chức năng và phạm vi tiếp cận dữ liệu được khu biệt rõ ràng:
*   **Quản trị viên / Chủ cửa hàng (Administrator/Manager):** Là nhóm đối tượng nắm quyền điều hành toàn diện hệ thống. Quản trị viên chịu trách nhiệm thiết lập các tham số cấu hình hệ thống (như quy tắc tích điểm thành viên, phân hạng VIP, cấu hình định mức tồn kho an toàn, thiết lập tải trọng sản xuất tối đa của xưởng bếp), quản lý hồ sơ nhân sự (cấp phát, khóa tài khoản và phân quyền), kiểm soát dòng tiền thông qua phân hệ Sổ quỹ thu chi và phê duyệt các báo cáo tài chính cấp cao. Phạm vi truy cập dữ liệu của Quản trị viên là toàn phần (Full Access), bao gồm cả quyền truy xuất Nhật ký hoạt động hệ thống (Audit Logs) để thực hiện giám sát hậu kiểm.
*   **Nhân viên thu ngân / Bán hàng (Cashier/Seller):** Đảm nhận vai trò trực tiếp giao dịch với khách hàng tại điểm bán (POS). Thu ngân có trách nhiệm lập hóa đơn bán lẻ siêu tốc tại quầy, tư vấn và ghi nhận đơn bánh tùy chỉnh, quản lý hồ sơ thông tin khách hàng, tích lũy điểm thưởng và thực hiện mở/đóng ca làm việc đối soát. Phạm vi truy cập dữ liệu của thu ngân được giới hạn nghiêm ngặt trong phân hệ bán hàng, quản lý đơn hàng và dữ liệu khách hàng. Nhóm đối tượng này tuyệt đối không được phép tiếp cận báo cáo doanh thu - lợi nhuận tổng thể của cửa hàng, không được can thiệp vào định lượng công thức kỹ thuật hoặc tự ý điều chỉnh thông tin tồn kho trong hệ thống.
*   **Thợ bánh / Nhân viên bếp (Baker/Chef):** Là lực lượng trực tiếp thực hiện quá trình sản xuất bánh dựa trên các yêu cầu từ hệ thống. Thợ bánh theo dõi danh sách đơn hàng cần sản xuất thông qua màn hình điều phối sản xuất (KDS), tra cứu công thức định lượng chi tiết cho từng loại bánh và cập nhật trạng thái tiến độ hoàn thành. Phạm vi truy cập dữ liệu của thợ bánh được khoanh vùng trong phân hệ sản xuất và danh mục công thức bánh, đảm bảo không bị phân tâm bởi các số liệu thương mại hay tài chính của tiệm bánh.
*   **Nhân viên kho (Warehouse Keeper):** Chịu trách nhiệm bảo đảm tính chính xác và an toàn của chuỗi cung ứng vật tư. Nhân viên kho thực hiện các thao tác lập phiếu nhập kho nguyên liệu từ nhà cung cấp, lập phiếu xuất kho phục vụ sản xuất cho bếp bánh, lập phiếu xuất hủy đối với vật tư hư hỏng hoặc hết hạn sử dụng, và cập nhật thông tin nhà cung cấp. Phạm vi truy cập dữ liệu của nhân viên kho chỉ giới hạn trong phân hệ quản lý kho hàng và danh mục vật tư, bị chặn hoàn toàn đối với việc lập hóa đơn bán hàng trực tiếp hay truy cập báo cáo tài chính phi nhân sự.

### 5.1.3. Các phân hệ chức năng cốt lõi
Để hiện thực hóa các mục tiêu quản lý phức tạp một cách đồng bộ, hệ thống được cấu trúc thành bốn phân hệ cốt lõi với sự phân định trách nhiệm rõ ràng nhưng chia sẻ dữ liệu nhất quán dưới tầng cơ sở dữ liệu:
*   **Phân hệ Quản lý bán hàng và xử lý giao dịch tại quầy:** Đây là phân hệ trực quan nhất, hỗ trợ nhân viên bán hàng thực hiện tính tiền nhanh tại quầy và thiết lập đơn hàng bánh tùy chỉnh. Phân hệ được thiết kế với cơ chế tính toán giá trị thời gian thực đối với bánh tùy chỉnh – tự động cộng dồn các loại phụ phí bao gồm kích thước, cốt bánh và nhân bánh trên nền giá cơ bản. Đồng thời, phân hệ tích hợp quy tắc kiểm soát đặt cọc nghiêm ngặt ở tầng nghiệp vụ nhằm bảo đảm thu tối thiểu một nửa tổng giá trị trước khi lưu đơn, vận hành quy trình đóng ca đối soát mù độc đáo, và kết xuất in biên lai nhiệt chuẩn tám mươi milimét thông qua các biểu mẫu báo cáo trực quan và chuyên nghiệp.
*   **Phân hệ Quản lý danh mục sản phẩm, vật tư và kho hàng:** Phân hệ này đóng vai trò hạt nhân trong việc duy trì chuỗi cung ứng nội bộ của tiệm bánh. Phân hệ cho phép quản lý danh mục sản phẩm kết hợp định lượng công thức chi tiết, tự động dự phóng sản lượng bánh tối đa dựa trên tồn kho thực tế của các nguyên liệu thành phần. Đặc biệt, phân hệ áp dụng triệt để cơ chế xuất kho theo nguyên tắc hết hạn trước, xuất trước thông qua các thủ tục lưu trữ thông minh trong cơ sở dữ liệu, đồng thời kiểm soát tải trọng sản xuất của xưởng bếp để ngăn chặn tình trạng quá tải đơn hàng nhận về.
*   **Phân hệ Báo cáo thống kê doanh thu, chi phí và hiệu suất hoạt động:** Cung cấp bức tranh toàn cảnh về sức khỏe tài chính của doanh nghiệp. Phân hệ thực hiện đối chiếu tự động giữa doanh thu thu về từ các hóa đơn bán lẻ với giá vốn định lượng của từng mã bánh để tính toán biên lợi nhuận thực tế. Bên cạnh đó, phân hệ tổng hợp dòng tiền từ phân hệ Sổ quỹ bao gồm các phiếu thu và phiếu chi phát sinh ngoài hoạt động bán hàng để kết xuất báo cáo lợi nhuận ròng chính xác, cùng các báo cáo biến động tồn kho chi tiết bao gồm số liệu đầu kỳ, nhập, xuất, hủy và cuối kỳ, hỗ trợ tối đa cho việc hoạch định kế hoạch mua sắm nguyên liệu.
*   **Phân hệ Quản lý thông tin khách hàng và chương trình tri ân:** Hướng đến việc tối đa hóa giá trị vòng đời của khách hàng. Phân hệ cho phép lưu trữ tập trung dữ liệu hội viên, tra cứu thông tin nhanh qua tên hoặc số điện thoại, và theo dõi lịch sử mua hàng chi tiết. Điểm nổi bật là phân hệ tích hợp cơ chế tích lũy điểm tự động dưới tầng cơ sở dữ liệu thông qua các trình kích hoạt tự động, nâng cấp hạng thành viên thân thiết khi đạt ngưỡng tích lũy quy định, và tự động áp dụng các tỷ lệ chiết khấu ưu đãi tương ứng trực tiếp khi lập hóa đơn tại điểm bán lẻ.

### 5.1.4. Thiết kế giao diện và trải nghiệm người dùng
*   **Nguyên lý thiết kế giao diện tổng thể:** Thiết kế giao diện của hệ thống tuân thủ hệ màu sắc ấm áp, mang lại cảm giác thân thiện, ngon miệng của các sản phẩm lò bánh, đồng thời tôn vinh vẻ đẹp sang trọng, tinh tế của thương hiệu. Màu cam hổ phách ấm được chọn làm màu chủ đạo tạo điểm nhấn kích thích thị giác, kết hợp hài hòa với màu xanh navy làm màu phụ trợ đại diện cho tính ổn định, chuyên nghiệp tại các thanh bên và bảng điều khiển. Nền ứng dụng sử dụng màu kem dịu nhẹ để giảm mỏi mắt cho nhân viên khi tương tác liên tục nhiều giờ, đi kèm các thẻ thông tin màu trắng thuần khiết và phông chữ hiện đại để tạo chiều sâu và cấu trúc rõ ràng.
*   **Giải pháp nhất quán và cơ chế điều hướng:** Nhằm tối ưu hóa hiệu suất thao tác và loại bỏ các thao tác thừa, ứng dụng triển khai mô hình kiến trúc khung ứng dụng nhất quán với một thanh điều hướng cố định nằm bên trái giao diện chính, phân quyền hiển thị động dựa trên vai trò của người dùng. Cơ chế này loại bỏ hoàn toàn việc sử dụng các nút quay lại rườm rà ở các màn hình con, giúp người dùng chuyển đổi linh hoạt giữa các phân hệ làm việc chỉ với một lần nhấp chuột. Mọi giao diện con đều được nhúng và hiển thị trong một vùng chứa trung tâm thống nhất, đảm bảo tính liên tục của trải nghiệm và loại bỏ hoàn toàn các cửa sổ bật lên chồng chéo dễ gây nhầm lẫn trong quá trình bán hàng và vận hành kho. Giao diện được định kiểu hoàn toàn bằng tệp định dạng trang trí riêng biệt, cấm tuyệt đối việc sử dụng định dạng trực tiếp trong tệp giao diện nhằm đảm bảo tính bảo trì cao và dễ dàng tùy biến giao diện trong tương lai.

---

## 5.2. Kiến trúc phần mềm
* **5.2.1. Mô hình kiến trúc tổng thể Model-View-Presenter**
  
  #### Định nghĩa mô hình kiến trúc Model-View-Presenter
  Mô hình kiến trúc Model-View-Presenter là một mô hình thiết kế phần mềm tiên tiến, được phát triển nhằm mục đích phân rã hệ thống thành các thành phần độc lập tương đối, từ đó tối ưu hóa việc tổ chức mã nguồn và nâng cao khả năng bảo trì cho các ứng dụng có giao diện người dùng phức tạp. Mô hình này chia hệ thống thành ba thành phần cốt lõi với các vai trò và trách nhiệm được phân định rõ rệt:
  *   **Thành phần View:** Đảm nhận vai trò hiển thị trực quan các thông tin dữ liệu cấu trúc và tiếp nhận các hành vi tương tác vật lý trực tiếp từ người dùng. Trong các ứng dụng máy trạm hiện đại, thành phần này được giữ ở trạng thái thụ động tối đa, hoàn toàn không chứa bất kỳ logic xử lý nghiệp vụ hay các câu lệnh truy vấn trực tiếp đến hệ cơ sở dữ liệu bên dưới.
  *   **Thành phần Presenter:** Đóng vai trò là bộ não điều phối luồng logic giao diện, đóng vai trò cầu nối trung gian duy nhất giữa View và Model. Presenter tiếp nhận các tín hiệu sự kiện từ View, thực hiện các biến đổi dữ liệu cần thiết ở mức giao diện, yêu cầu tầng dịch vụ xử lý nghiệp vụ và đẩy kết quả trở lại để cập nhật trạng thái hiển thị trực quan cho View.
  *   **Thành phần Model:** Là nơi lưu trữ toàn bộ các thực thể dữ liệu cấu trúc, các quy tắc nghiệp vụ cốt lõi và cơ chế giao tiếp với nguồn lưu trữ thông tin vật lý của hệ thống. Thành phần này được thiết kế hoàn toàn độc lập với giao diện người dùng, cho phép tái sử dụng linh hoạt trong các môi trường triển khai khác nhau.

  #### Lý luận khoa học khi lựa chọn kiến trúc này cho dự án
  Việc áp dụng mô hình kiến trúc Model-View-Presenter cho Hệ thống Quản lý Tiệm bánh Chuyên nghiệp được xây dựng trên những lập luận khoa học và thực tiễn sâu sắc sau đây:
  *   **Ngăn chặn sự phụ thuộc chéo và phá vỡ cấu trúc lập trình:** Trong các dự án xây dựng trên nền tảng JavaFX thông thường, các tệp lập trình giao diện rất dễ rơi vào tình trạng quá tải mã nguồn khi các lập trình viên tích hợp cả logic hiển thị lẫn các câu lệnh truy vấn cơ sở dữ liệu vào cùng một lớp điều khiển giao diện. Mô hình này thiết lập một cơ chế cô lập tuyệt đối, buộc giao diện chỉ làm nhiệm vụ hiển thị và nhường toàn bộ quyền quyết định logic cho Presenter.
  *   **Tối ưu hóa khả năng kiểm thử độc lập:** Do Presenter được liên kết với View thông qua một giao diện trừu tượng thay vì tham chiếu trực tiếp đến các thành phần đồ họa cụ thể, đội ngũ phát triển có thể dễ dàng kiểm thử toàn bộ logic nghiệp vụ giao diện bằng cách giả lập các hành vi của View mà không cần phải khởi chạy giao diện đồ họa thực tế của hệ thống.
  *   **Đảm bảo tính độc lập của logic nghiệp vụ:** Các phân hệ nghiệp vụ phức tạp của tiệm bánh như tính toán công thức định lượng nguyên vật liệu hay quản lý kho theo nguyên tắc hết hạn trước xuất trước cần được bảo vệ trước những thay đổi liên tục của giao diện người dùng. Sự tách biệt này giúp hệ thống hoạt động ổn định và dễ dàng tái cấu trúc mà không gây ảnh hưởng dây chuyền.

  #### Phân tích ưu điểm và nhược điểm của mô hình kiến trúc
  
  **Các ưu điểm vượt trội:**
  *   *Phân tách trách nhiệm triệt để:* Đảm bảo tính đơn nhiệm của từng lớp lập trình, giúp mã nguồn trở nên sáng sủa, dễ đọc và giảm thiểu tối đa các xung đột mã nguồn khi nhiều thành viên cùng phát triển trên một phân hệ.
  *   *Khả năng bảo trì và nâng cấp linh hoạt:* Khi có sự thay đổi về giao diện người dùng, lập trình viên chỉ cần can thiệp vào tầng View mà không sợ làm đứt gãy hay sai lệch các quy tắc nghiệp vụ tài chính hoặc kho quỹ bên dưới.
  *   *Đơn giản hóa việc gỡ lỗi:* Nhờ sự phân chia ranh giới rõ ràng, khi phát sinh lỗi hệ thống, lập trình viên có thể khoanh vùng nguyên nhân một cách nhanh chóng dựa trên luồng đi của dữ liệu giữa ba thành phần.

  **Các nhược điểm và hạn chế:**
  *   *Gia tăng độ phức tạp ban đầu của dự án:* Do phải thiết lập hệ thống giao diện trừu tượng và tách biệt các lớp điều khiển, số lượng tệp tin mã nguồn và các lớp trung gian tăng lên đáng kể so với mô hình phát triển tuyến tính thông thường.
  *   *Đường cong học tập và yêu cầu tính kỷ luật cao:* Đòi hỏi đội ngũ phát triển phải thấu hiểu sâu sắc và tuân thủ nghiêm ngặt các nguyên tắc phân tầng, tránh xu hướng viết tắt hoặc tích hợp logic sai lệch vào View do thói quen cũ.
  *   *Chi phí giao tiếp giữa các thành phần:* Việc liên tục truyền tải dữ liệu qua lại giữa View và Presenter thông qua các giao diện trung gian có thể tạo ra một lượng mã nguồn đệm tương đối lớn, đòi hỏi quá trình thiết kế ban đầu phải được chuẩn hóa kỹ lưỡng.

  #### Sơ đồ tương tác và cơ chế trao đổi thông tin
  Mối quan hệ giao tiếp giữa ba thành phần View, Presenter và Model tuân thủ cơ chế giao tiếp hai chiều gián tiếp. View không bao giờ nói chuyện trực tiếp với Model và ngược lại. Mọi luồng thông tin bắt buộc phải đi qua Presenter để đảm bảo tính cô lập và bảo mật thông tin.

  ```mermaid
  sequenceDiagram
      autonumber
      actor NguoiDung as Người dùng
      participant View as View (Controller JavaFX)
      participant Presenter as Presenter
      participant Model as Model (Service / DAO / DTO)
      participant DB as Cơ sở dữ liệu Oracle

      NguoiDung->>View: Tương tác vật lý trên giao diện
      View->>Presenter: Kích hoạt sự kiện giao diện thông qua interface
      Presenter->>Model: Gọi logic nghiệp vụ tương ứng
      Model->>DB: Truy vấn dữ liệu qua JDBC hoặc thủ tục lưu trữ
      DB-->>Model: Trả kết quả truy vấn
      Model-->>Presenter: Trả về kết quả dưới dạng đối tượng DTO hoặc danh sách
      Presenter->>View: Cập nhật dữ liệu hiển thị và thay đổi trạng thái giao diện
      View-->>NguoiDung: Phản hồi trực quan trên màn hình
  ```

  #### Ví dụ minh họa luồng vận hành thực tế
  Để minh họa trực quan cách thức vận hành của mô hình kiến trúc này, chúng ta phân tích luồng xử lý của chức năng **Lập hóa đơn bán lẻ** khi nhân viên thu ngân thực hiện bấm nút hoàn tất thanh toán cho đơn hàng tại quầy:
  1.  **Bước 1 - Tiếp nhận tương tác tại View:** Nhân viên thu ngân bấm nút "Thanh toán" trên màn hình bán hàng. Lớp điều khiển giao diện đóng vai trò là View tiếp nhận sự kiện click chuột này. View thực hiện kiểm tra sơ bộ các thông tin nhập liệu trên giao diện và kích hoạt phương thức xử lý thanh toán trên Presenter thông qua giao diện lập trình được định nghĩa trước.
  2.  **Bước 2 - Điều phối và xác thực tại Presenter:** Presenter nhận tín hiệu yêu cầu thanh toán từ View. Nó thu thập các thông tin sản phẩm và khách hàng hiện tại dưới dạng các đối tượng truyền dữ liệu thuần túy và chuyển giao yêu cầu xử lý nghiệp vụ xuống cho tầng dịch vụ bên dưới thuộc thành phần Model.
  3.  **Bước 3 - Thực thi quy tắc nghiệp vụ tại Model:** Tầng dịch vụ tiếp nhận yêu cầu, thực hiện các phép tính toán tài chính như áp dụng chính sách giảm giá của hạng thành viên VIP và tính toán điểm tích lũy mới. Sau đó, nó gọi lớp truy cập dữ liệu để thực thi thủ tục lưu trữ hóa đơn trong cơ sở dữ liệu Oracle thông qua kết nối JDBC an toàn.
  4.  **Bước 4 - Lưu trữ và phản hồi dữ liệu:** Cơ sở dữ liệu Oracle thực hiện ghi nhận hóa đơn, cập nhật số dư kho nguyên liệu theo nguyên tắc hết hạn trước xuất trước và trả về trạng thái giao dịch thành công. Tầng dữ liệu đóng gói kết quả thành một đối tượng truyền dữ liệu hóa đơn hoàn chỉnh và trả ngược về cho Presenter.
  5.  **Bước 5 - Cập nhật giao diện trực quan:** Presenter tiếp nhận kết quả thành công, kích hoạt lệnh in hóa đơn nhiệt qua JasperReports, đồng thời ra lệnh cho View xóa giỏ hàng hiện tại, làm trống các trường nhập liệu và hiển thị thông báo giao dịch thành công trực quan tới người dùng.
* **5.2.2. Chi tiết cấu trúc phân tầng ứng dụng**
  * **Tầng View (Giao diện):** Đảm nhiệm việc hiển thị trực quan và tiếp nhận các sự kiện tương tác từ người dùng thông qua công nghệ JavaFX.
  * **Tầng Presenter (Điều phối):** Thành phần trung gian xử lý luồng logic giao diện, tiếp nhận yêu cầu từ View, gọi dịch vụ ở tầng dưới và cập nhật trạng thái hiển thị cho View.
  * **Tầng Service (Nghiệp vụ):** Nơi tập trung toàn bộ các quy tắc nghiệp vụ, tính toán logic và xử lý dữ liệu của hệ thống độc lập với giao diện.
  * **Tầng DAO (Data Access Object):** Thực hiện các truy vấn dữ liệu trực tiếp đến hệ quản trị cơ sở dữ liệu thông qua các kết nối JDBC và thủ tục lưu trữ.
  * **Tầng DTO (Data Transfer Object):** Các đối tượng chứa dữ liệu thuần túy, đóng vai trò vận chuyển thông tin an toàn giữa các tầng kiến trúc.
* **5.2.3. Giải pháp kết nối và lưu trữ cơ sở dữ liệu**

  #### Cơ chế thiết lập và quản lý các kết nối cơ sở dữ liệu
  Hệ thống thiết lập kết nối trực tiếp đến cơ sở dữ liệu Oracle thông qua trình điều khiển kết nối tiêu chuẩn của Java. Các thông tin cấu hình kết nối bao gồm địa chỉ máy chủ, tên tài khoản quản trị và mật khẩu truy cập được quản lý tập trung trong tệp thuộc tính ứng dụng để đảm bảo tính linh hoạt khi triển khai trên các máy tính khác nhau.
  
  Lớp kết nối cơ sở dữ liệu của hệ thống sử dụng cơ chế kết nối trực tiếp thông qua trình quản lý trình điều khiển tích hợp sẵn của Java, kết nối tới đường dẫn cơ sở dữ liệu dạng mỏng với tài khoản quản trị dữ liệu tiệm bánh. Để bảo vệ tài nguyên hệ thống và ngăn ngừa hiện tượng rò rỉ kết nối, dự án áp dụng nghiêm ngặt cú pháp thử nghiệm tự động giải phóng tài nguyên của Java. Các kết nối, câu lệnh truy vấn và tập kết quả sau khi hoàn thành nhiệm vụ sẽ tự động được đóng lại. Ngoài ra, lớp kết nối cũng cung cấp các phương thức đóng tài nguyên thủ công an toàn để dự phòng cho các trường hợp đặc biệt.

  #### Quy trình thực thi các thủ tục lưu trữ và hàm trong cơ sở dữ liệu
  Nhằm tối ưu hóa hiệu năng và bảo vệ tính toàn vẹn của dữ liệu, toàn bộ các logic thay đổi dữ liệu hoặc tính toán phức tạp đều không thực hiện trực tiếp trên giao diện mà được ủy quyền cho các thủ tục lưu trữ và hàm chạy trực tiếp trong cơ sở dữ liệu Oracle:
  *   **Thực thi thủ tục lưu trữ và hàm:** Tầng truy cập dữ liệu của Java sử dụng các câu lệnh gọi hàm chuyên dụng để truyền tham số và kích hoạt các thủ tục lưu trữ dưới cơ sở dữ liệu. Các tham số đầu vào và đầu ra được định kiểu rõ ràng, bao gồm cả việc truyền các cấu trúc dữ liệu phức tạp như danh sách dưới dạng chuỗi văn bản định dạng chung để cơ sở dữ liệu phân tách trực tiếp bằng các hàm phân tích tích hợp sẵn dưới cơ sở dữ liệu.
  *   **Quản lý giao dịch và kiểm soát lỗi:** Mọi thủ tục lưu trữ thực hiện các thao tác ghi dữ liệu đều được bao bọc trong các khối xử lý ngoại lệ nghiêm ngặt. Khi xảy ra bất kỳ lỗi phát sinh nào, cơ sở dữ liệu sẽ tự động thực hiện lệnh hoàn tác toàn bộ các thay đổi chưa hoàn tất để tránh trạng thái dữ liệu không đồng nhất. Đồng thời, cơ sở dữ liệu sẽ phát ra các thông điệp lỗi tiếng Việt có dấu tùy chỉnh để truyền ngược lại cho ứng dụng hiển thị trực quan đến người dùng. Lệnh xác nhận hoàn thành giao dịch chỉ được gọi duy nhất một lần ở cuối quy trình ngay trước khối bắt lỗi ngoại lệ.

---

## 5.3. Công nghệ sử dụng

### 5.3.1. Ngôn ngữ lập trình chính
Hệ thống được phát triển trên nền tảng Java phiên bản 21, đây là phiên bản hỗ trợ dài hạn có tính ổn định cao và hiệu năng tối ưu. Việc sử dụng phiên bản Java hiện đại giúp dự án khai thác hiệu quả các đặc tính lập trình tiên tiến:
*   **Quản lý tài nguyên an toàn:** Sử dụng cú pháp thử nghiệm tự động giải phóng tài nguyên của Java để tự động quản lý đóng các luồng dữ liệu và kết nối cơ sở dữ liệu, loại bỏ hoàn toàn nguy cơ rò rỉ bộ nhớ.
*   **Kiểu dữ liệu thời gian hiện đại:** Áp dụng các lớp quản lý ngày và thời gian hiện đại của Java thay thế cho các lớp quản lý thời gian cũ đã lỗi thời. Việc này giúp việc tính toán thời gian chuẩn bị bánh và đối soát ca làm việc của thu ngân đạt độ chính xác tuyệt đối.
*   **Xử lý đa luồng nâng cao:** Tận dụng các tác vụ chạy nền và dịch vụ tích hợp sẵn để xử lý các nghiệp vụ nặng như kết xuất báo cáo và in hóa đơn nhiệt mà không gây tắc nghẽn giao diện đồ họa chính của hệ thống.

### 5.3.2. Công nghệ xây dựng giao diện người dùng
Giao diện của hệ thống được xây dựng trên sự kết hợp giữa các công cụ giao diện hiện đại để đảm bảo trải nghiệm người dùng tối ưu:
*   **Thư viện đồ họa JavaFX:** Dự án sử dụng thư viện JavaFX phiên bản 25 để xây dựng giao diện đồ họa. Cấu trúc giao diện được định nghĩa rõ ràng thông qua ngôn ngữ định dạng dựa trên cấu trúc XML, giúp tách biệt hoàn toàn bố cục tĩnh của màn hình khỏi lớp mã nguồn điều khiển sự kiện tương tác.
*   **Định dạng giao diện bằng tệp trang trí riêng:** Để đồng bộ hóa trải nghiệm người dùng theo hệ màu cam hổ phách ấm áp, toàn bộ các quy chuẩn định kiểu được quản lý tập trung trong một tệp định dạng trang trí CSS duy nhất. Dự án tuân thủ nghiêm ngặt việc cấm định dạng trực tiếp trong tệp giao diện để đảm bảo tính dễ bảo trì và khả năng mở rộng giao diện linh hoạt trong tương lai.

### 5.3.3. Hệ quản trị cơ sở dữ liệu
Hệ thống sử dụng hệ quản trị cơ sở dữ liệu Oracle phiên bản 12c trở lên để lưu trữ toàn bộ dữ liệu nghiệp vụ của tiệm bánh:
*   **Chuẩn hóa dữ liệu chặt chẽ:** Các bảng dữ liệu được thiết kế tuân thủ các quy tắc chuẩn hóa cao, thiết lập đầy đủ các ràng buộc khóa ngoại và ràng buộc kiểm tra điều kiện nghiệp vụ để đảm bảo dữ liệu luôn hợp lệ ngay từ tầng cơ sở dữ liệu.
*   **Xử lý nghiệp vụ tập trung:** Khai thác tối đa hiệu năng của cơ sở dữ liệu bằng cách đóng gói các logic nghiệp vụ thay đổi dữ liệu vào các thủ tục lưu trữ và hàm chạy trực tiếp trong Oracle. Đồng thời, dự án sử dụng các trình kích hoạt tự động để tự động hóa các tác vụ ngầm như tính điểm thành viên và nâng hạng thành viên thân thiết trực tiếp dưới cơ sở dữ liệu.

### 5.3.4. Công cụ quản lý dự án và đóng gói
Hệ thống sử dụng công cụ quản lý dự án Maven để tự động hóa toàn bộ vòng đời phát triển phần mềm:
*   **Quản lý thư viện phụ thuộc tập trung:** Maven chịu trách nhiệm tự động tải về và quản lý các thư viện phụ thuộc từ bên thứ ba bao gồm trình điều khiển kết nối cơ sở dữ liệu Oracle, thư viện chuyển đổi chuỗi văn bản Gson và thư viện kết xuất báo cáo in ấn hóa đơn.
*   **Chuẩn hóa cấu trúc dự án:** Giúp tổ chức thư mục mã nguồn và tài nguyên theo một khuôn mẫu thống nhất, đồng thời hỗ trợ đóng gói toàn bộ ứng dụng thành một tệp lưu trữ Java có khả năng thực thi độc lập chỉ bằng một dòng lệnh đơn giản.

---

## 5.4. Môi trường lập trình và triển khai

### 5.4.1. Môi trường phát triển ứng dụng
Quá trình xây dựng và phát triển hệ thống được thực hiện trên môi trường lập trình tiêu chuẩn:
*   **Hệ điều hành:** Microsoft Windows 10 hoặc Windows 11.
*   **Bộ công cụ phát triển phần mềm:** Java Development Kit phiên bản 21.
*   **Công cụ phát triển tích hợp:** IntelliJ IDEA phiên bản Community hoặc Ultimate.
*   **Hệ thống quản lý mã nguồn:** Git kết hợp nền tảng lưu trữ mã nguồn trực tuyến để quản lý phiên bản và phối hợp nhóm.

### 5.4.2. Môi trường kiểm thử hệ thống
Công tác kiểm thử hệ thống nhằm đảm bảo các chức năng hoạt động đúng theo đặc tả nghiệp vụ được tiến hành trên môi trường giả lập:
*   **Cấu hình phần cứng kiểm thử:** Máy tính cá nhân trang bị bộ vi xử lý đa nhân tốc độ cao, tối thiểu 8 gigabyte bộ nhớ trong để chạy đồng thời ứng dụng JavaFX và máy chủ cơ sở dữ liệu.
*   **Cấu hình phần mềm kiểm thử:** Chạy giả lập máy chủ cơ sở dữ liệu Oracle tại cổng kết nối mặc định trên môi trường local. Toàn bộ các kịch bản kiểm thử đơn vị, kiểm thử tích hợp giao diện và dữ liệu được chạy trực tiếp trên môi trường này.

### 5.4.3. Môi trường triển khai thực tế
Để vận hành ổn định trong môi trường thực tế tại tiệm bánh, hệ thống yêu cầu cấu hình tối thiểu như sau:
*   **Máy chủ cơ sở dữ liệu:** Hệ điều hành máy chủ thông dụng, cài đặt Oracle Database phiên bản 12c trở lên, kết nối mạng nội bộ ổn định để phục vụ các yêu cầu truy vấn đồng thời từ nhiều máy trạm.
*   **Máy trạm của nhân viên:** Máy tính hoặc máy tính bảng chạy hệ điều hành Windows, cài đặt môi trường thực thi Java phiên bản 21, kết nối trực tiếp đến máy in hóa đơn nhiệt khổ 80 milimét để in biên lai bán hàng tức thời.

---

## 5.5. Thuật toán áp dụng

### 5.5.1. Thuật toán mã hóa và bảo mật thông tin
Bảo mật thông tin nhân viên và dữ liệu hệ thống được đặt lên hàng đầu thông qua các thuật toán mã hóa tiêu chuẩn:
*   **Thuật toán băm mật khẩu:** Sử dụng thuật toán băm một chiều BCrypt với độ muối là 12 để băm mật khẩu của nhân viên trước khi ghi nhận vào cơ sở dữ liệu Oracle. Cơ chế này tự động thêm chuỗi muối ngẫu nhiên để chống lại các cuộc tấn công dò quét từ điển.
*   **Xác thực mật khẩu thông minh:** Hệ thống hỗ trợ phương thức đối chiếu thông minh, tự động kiểm tra xem chuỗi mật khẩu trong cơ sở dữ liệu có phải là mã băm BCrypt hay không để áp dụng so sánh băm, đồng thời hỗ trợ so sánh trực tiếp để tương thích với các tài khoản thử nghiệm cũ chưa được mã hóa.

### 5.5.2. Thuật toán tìm kiếm và tối ưu hóa truy vấn
Nhằm đáp ứng yêu cầu khắt khe về mặt thời gian phản hồi thực tế tại quầy giao dịch (Point of Sale - POS) – đảm bảo thời gian phản hồi (latency) luôn dưới 1 giây đối với các tác vụ tra cứu dữ liệu của nhân viên thu ngân, hệ thống Bakery Management System (BMS) đã triển khai đồng bộ các giải pháp tối ưu hóa truy vấn thực tế bám sát kiến trúc và đặc thù dữ liệu của dự án:

#### a. Giải pháp tìm kiếm thông tin khách hàng siêu tốc (Customer Search & Query Tuning)
Trong hoạt động bán hàng trực tiếp, việc tra cứu nhanh hồ sơ khách hàng khi lập hóa đơn là cực kỳ quan trọng. Hệ thống thực hiện tối ưu hóa tác vụ này thông qua cơ chế kết hợp giữa tầng cơ sở dữ liệu Oracle và tầng điều phối ứng dụng (Presenter):
*   **Chỉ mục duy nhất tự động (Automatic Unique Indexing) trên cột số điện thoại (`SDT`):** 
    Cột `SDT` của bảng `KHACHHANG` được thiết lập ràng buộc duy nhất (`UNIQUE NOT NULL`). Hệ quản trị cơ sở dữ liệu Oracle tự động khởi tạo một chỉ mục duy nhất (Unique B-Tree Index) trên cột này. Khi nhân viên thu ngân nhập số điện thoại của hội viên để tra cứu trước thanh toán, hệ thống thực thi câu lệnh SQL tìm kiếm chính xác qua `PreparedStatement`. Bộ tối ưu hóa truy vấn của Oracle áp dụng chiến lược quét chỉ mục duy nhất (`INDEX UNIQUE SCAN`) với độ phức tạp thuật toán cực đại là $O(\log N)$, phản hồi thông tin hội viên tức thời trong vòng từ 1ms đến 5ms.
*   **Tham số hóa truy vấn và ngăn ngừa SQL Injection bằng `PreparedStatement`:**
    Tầng dữ liệu `KhachHangDAO` sử dụng câu lệnh SQL thực tế sau để thực hiện tra cứu khách hàng theo từ khóa đa cột:
    ```sql
    SELECT KH.*, HTV.TENHANG AS TENHANG, HTV.PHANTRAMGIAMGIA AS PHANTRAMGIAMGIA, NV.HOTEN AS TENNX 
    FROM KHACHHANG KH 
    LEFT JOIN HANGTHANHVIEN HTV ON KH.MAHANG = HTV.MAHANG 
    LEFT JOIN NHANVIEN NV ON KH.MANX = NV.MANV 
    WHERE KH.THOIDIEMXOA IS NULL AND (LOWER(KH.HOTEN) LIKE ? OR KH.SDT LIKE ? OR TO_CHAR(KH.MAKH) LIKE ? OR LOWER(KH.DIACHI) LIKE ?) 
    ORDER BY KH.MAKH DESC
    ```
    Mọi tham số tìm kiếm được gán động qua `PreparedStatement.setString()`, ngăn chặn hoàn toàn lỗ hổng bảo mật SQL Injection. Đồng thời, cấu trúc câu lệnh nhất quán giúp hệ quản trị cơ sở dữ liệu Oracle tái sử dụng kế hoạch thực thi (Execution Plan) đã biên dịch sẵn trong Shared Pool (thực hiện Soft Parse thay vì Hard Parse), giảm thiểu thời gian xử lý truy vấn trên máy chủ.
*   **Bộ lọc tìm kiếm và xử lý nghiệp vụ tối ưu tại tầng Presenter (In-Memory Filtering):**
    Để triệt tiêu hoàn toàn độ trễ do đường truyền mạng khi nhân viên gõ phím tìm kiếm liên tục, hệ thống áp dụng kỹ thuật lọc dữ liệu trong bộ nhớ (In-Memory Filtering) tại lớp `KhachHangPresenter`. Dữ liệu khách hàng ban đầu được tải một lần từ cơ sở dữ liệu lên bộ nhớ máy trạm, sau đó các thao tác lọc bổ sung theo hạng thành viên hoặc khoảng thời gian đăng ký được xử lý trực tiếp trên các dòng dữ liệu bằng Java Streams:
    ```java
    duLieuSauLoc = duLieuSauTimKiem.stream()
            .filter(this::khopLocNgay)
            .filter(this::khopLocHang)
            .toList();
    ```
    Giải pháp này giúp giảm thiểu tối đa số lần thực hiện các kết nối mạng (round-trips) không cần thiết tới cơ sở dữ liệu, đảm bảo tốc độ phản hồi cực nhanh (<10ms).

#### b. Giải pháp phân trang dữ liệu và giới hạn tải thông minh (Smart UI Pagination & Row Limiting)
Khi hệ thống vận hành lâu dài, lượng dữ liệu tích lũy từ các phân hệ sẽ rất lớn. BMS áp dụng phương án phân vùng tải dữ liệu thông minh để bảo vệ hiệu năng máy trạm và đường truyền mạng:
*   **Phân trang tại tầng Presenter tối ưu hóa hiển thị giao diện (UI Pagination):**
    Đối với các danh mục quản lý trực quan trên giao diện như danh sách khách hàng, hệ thống thực hiện phân trang trực tiếp tại Presenter (`KhachHangPresenter.capNhatTrangHienTai`) bằng phương thức cắt danh sách `subList` theo hằng số giới hạn dòng (`SO_DONG_MOI_TRANG = 10`):
    ```java
    int tuChiSo = (trangHienTai - 1) * SO_DONG_MOI_TRANG;
    int denChiSo = Math.min(tuChiSo + SO_DONG_MOI_TRANG, duLieuSauLoc.size());
    List<KhachHangDTO> trang = duLieuSauLoc.subList(tuChiSo, denChiSo);
    view.hienThiDanhSachKhachHang(trang);
    ```
    Việc giới hạn TableView chỉ phải vẽ và quản lý một số lượng rất ít dòng dữ liệu tại một thời điểm giúp giải phóng bộ nhớ RAM máy trạm, giảm áp lực dọn dẹp cho bộ thu gom rác (Garbage Collector - GC) của Java, giữ cho tốc độ kết xuất giao diện đồ họa đồ thị JavaFX mượt mà ổn định ở mức 60 FPS.
*   **Giới hạn số lượng dòng tải trực tiếp từ cơ sở dữ liệu (Database-Level Row Limiting):**
    Đối với các bảng nhật ký lịch sử hệ thống (`LICHSUHETHONG`), phiếu thu chi (`PHIEUTHUCHI`), phiếu nhập kho (`PHIEUNHAPKHO`), hoặc phiếu xuất kho (`PHIEUXUATKHO`) tích lũy hàng trăm ngàn dòng dữ liệu theo thời gian, việc tải toàn bộ bản ghi lên bộ nhớ máy trạm là không khả thi và dễ gây lỗi tràn bộ nhớ (`OutOfMemoryError`). 
    
    Hệ thống tối ưu hóa tác vụ này bằng cách bọc mệnh đề giới hạn dòng của Oracle (`FETCH FIRST N ROWS ONLY`) trực tiếp trong các câu lệnh truy vấn của tầng DAO (ví dụ: `HoatDongNhanVienDAO` giới hạn 500 bản ghi mới nhất, `PhieuThuChiDAO` giới hạn 50 hoặc 100 bản ghi mới nhất):
    ```sql
    -- Giới hạn số lượng dòng lịch sử hoạt động nhân viên tải lên:
    SELECT * FROM HOATDONGNHANVIEN 
    ORDER BY THOIGIAN DESC 
    FETCH FIRST 500 ROWS ONLY
    ```
    Giải pháp này giúp kiểm soát chặt chẽ dung lượng gói tin truyền tải trên đường truyền mạng nội bộ cửa hàng, đồng thời đảm bảo hệ thống luôn phản hồi cực nhanh dưới 1 giây mà vẫn đáp ứng đầy đủ nhu cầu tra cứu thông tin giao dịch gần đây của nhân viên.

### 5.5.3. Thuật toán nghiệp vụ đặc thù
*   **Thuật toán quản lý kho theo hạn sử dụng:** Sử dụng nguyên lý hết hạn trước, xuất trước. Khi thợ bếp hoặc thủ kho lập phiếu xuất kho nguyên liệu, hệ thống tự động quét và ưu tiên trừ số lượng tồn của các lô nguyên vật liệu có hạn sử dụng gần nhất.
*   **Thuật toán phân tích điểm nghẽn và dự phóng sản lượng tối đa (Production Capacity & Bottleneck Analysis):**
    Thuật toán vận hành dựa trên việc phân tích mối tương quan giữa định lượng cấu thành sản phẩm (Bill of Materials - BOM / Công thức kỹ thuật chuẩn hóa) và trữ lượng tồn kho nguyên vật liệu khả dụng trong thời gian thực. Khi thợ bánh thực hiện lập phiếu xuất kho phục vụ sản xuất, hệ thống tự động đối chiếu nhu cầu nguyên liệu của từng đơn vị thành phẩm với trữ lượng tồn kho hiện hành. Bằng cách áp dụng phép toán chia tối giản kết hợp hàm phần nguyên sàn ($\lfloor \dots \rfloor$) trên toàn bộ danh mục vật tư cấu thành, thuật toán xác định chính xác **nguyên liệu điểm nghẽn (Production Bottleneck)** – tức thành phần vật tư bị giới hạn và sẽ cạn kiệt đầu tiên, từ đó tính toán ra giới hạn sản lượng sản xuất tối đa khả dụng:
    $$N_{\max} = \min_{i \in \text{Ingredients}} \left( \left\lfloor \frac{\text{Tồn kho hiện tại}(i)}{\text{Định lượng tiêu chuẩn}(i)} \right\rfloor \right)$$
    Cơ chế dự phóng và cảnh báo chủ động này cung cấp số liệu trực quan về giới hạn năng lực sản xuất còn lại của bếp bánh, giúp thợ bánh chủ động điều phối kế hoạch sản xuất và hỗ trợ bộ phận mua hàng kịp thời bổ sung các nguyên liệu điểm nghẽn trước khi xảy ra hiện tượng đứt gãy chuỗi cung ứng vật tư nội bộ.
*   **Thuật toán tính giá bánh tùy chỉnh thời gian thực:** Tự động cộng dồn các loại phụ phí về kích thước, cốt bánh và nhân bánh dựa trên một công thức giá nền được cấu hình động dưới cơ sở dữ liệu, đảm bảo giá hiển thị liên tục và chính xác khi nhân viên thu ngân thao tác chọn tùy chọn trên màn hình.
*   **Thuật toán kiểm soát tải trọng xưởng bếp:** Tự động tính toán ngày nhận bánh trừ đi thời gian chuẩn bị định lượng để so sánh với công suất tối đa của bếp, tự động chặn không cho nhận đơn mới nếu bếp đã đạt ngưỡng giới hạn sản xuất trong ngày.

---

## 5.6. Thư viện từ bên thứ ba

Toàn bộ danh mục phụ thuộc được quản lý tập trung qua Apache Maven, đảm bảo tính nhất quán về phiên bản giữa các thành viên trong nhóm phát triển.

### 5.6.1. Thư viện kết nối và giao tiếp cơ sở dữ liệu
*   **Oracle JDBC Driver (`ojdbc8` v21.9.0.0):** Trình điều khiển kết nối chuẩn JDBC 4.2 do Oracle phát hành, cho phép tầng DAO thực thi truy vấn và gọi Stored Procedure qua kết nối "thin" thuần Java mà không cần cài Oracle Client.
*   **Google Gson (`gson` v2.11.0):** Thư viện tuần tự hóa đối tượng Java sang JSON. BMS dùng Gson để đóng gói danh sách chi tiết hóa đơn thành một chuỗi JSON duy nhất trước khi truyền xuống Stored Procedure Oracle xử lý một lần.

### 5.6.2. Thư viện xuất bản tài liệu và báo cáo
*   **JasperReports (`jasperreports` + `jasperreports-fonts` v6.21.3):** Framework in ấn và kết xuất báo cáo, được dùng để in hóa đơn nhiệt 80mm tại quầy và xuất báo cáo doanh thu, tồn kho sang PDF hỗ trợ đầy đủ tiếng Việt Unicode.
*   **iText (`itext` v2.1.7):** Thư viện tạo tệp PDF cấp thấp, hoạt động như thành phần phụ thuộc nội bộ của JasperReports trong quá trình kết xuất báo cáo, không được gọi trực tiếp từ mã nguồn BMS.
*   **Apache POI (`poi-ooxml` v5.3.0):** Thư viện đọc ghi tài liệu Microsoft Office. BMS sử dụng để xuất danh sách khách hàng sang định dạng `.xlsx` phục vụ nhu cầu phân tích nghiệp vụ của ban quản lý.

### 5.6.3. Thư viện bảo mật và xác thực
*   **jBCrypt (`jbcrypt` v0.4):** Cài đặt thuật toán băm mật khẩu BCrypt một chiều. BMS dùng để băm mật khẩu nhân viên trước khi lưu vào cơ sở dữ liệu và đối chiếu an toàn khi đăng nhập.
*   **JJWT (`jjwt-api/impl/jackson` v0.12.5):** Bộ thư viện tạo, ký số và xác thực JSON Web Token theo chuẩn RFC 7519. BMS dùng để quản lý phiên đăng nhập không trạng thái và phát hành mã OTP đặt lại mật khẩu.

### 5.6.4. Thư viện giao tiếp
*   **Jakarta Mail (`jakarta.mail` v2.0.1):** Thư viện gửi email qua giao thức SMTP. BMS tích hợp để tự động gửi mã OTP đặt lại mật khẩu đến địa chỉ email đã đăng ký của nhân viên.

