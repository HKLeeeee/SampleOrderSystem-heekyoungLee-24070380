package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.MainMenuView;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {

    private MainController controller;

    @BeforeEach
    void setUp() {
        controller = new MainController(new MainMenuView());
    }

    @Test
    @DisplayName("MainController 생성 성공")
    void mainController_생성_성공() {
        assertNotNull(controller);
    }

    @Test
    @DisplayName("MainMenuView 메뉴 문자열에 [1]~[6], [0] 포함")
    void mainMenuView_메뉴_출력_포함_문자열() {
        MainMenuView view = new MainMenuView();
        String menu = view.buildMainMenuText();

        assertTrue(menu.contains("[1]"), "메뉴에 [1] 포함");
        assertTrue(menu.contains("[2]"), "메뉴에 [2] 포함");
        assertTrue(menu.contains("[3]"), "메뉴에 [3] 포함");
        assertTrue(menu.contains("[4]"), "메뉴에 [4] 포함");
        assertTrue(menu.contains("[5]"), "메뉴에 [5] 포함");
        assertTrue(menu.contains("[6]"), "메뉴에 [6] 포함");
        assertTrue(menu.contains("[0]"), "메뉴에 [0] 포함");
    }

    @Test
    @DisplayName("유효 메뉴 번호 0~6 허용")
    void mainController_유효_메뉴_번호_0_6() {
        for (int i = 0; i <= 6; i++) {
            assertTrue(controller.isValidMenuChoice(i), i + " 는 유효한 메뉴 번호");
        }
    }

    @Test
    @DisplayName("유효 범위 외 메뉴 번호 거부")
    void mainController_유효_범위_외_거부() {
        assertFalse(controller.isValidMenuChoice(-1));
        assertFalse(controller.isValidMenuChoice(7));
        assertFalse(controller.isValidMenuChoice(99));
    }
}
