package controller;

import model.entity.Sample;
import model.service.SampleService;
import view.SampleView;

import java.util.List;

public class SampleController {

    private final SampleService service;
    private final SampleView view;

    public SampleController(SampleService service, SampleView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        while (true) {
            view.displaySampleMenu();
            String choice;
            try {
                choice = System.console() != null
                        ? System.console().readLine()
                        : new java.util.Scanner(System.in).nextLine();
            } catch (Exception e) { break; }

            switch (choice.trim()) {
                case "1" -> register();
                case "2" -> listAll();
                case "3" -> search();
                case "0" -> { return; }
                default -> view.displayMessage("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private void register() {
        try {
            Sample s = view.inputNewSample();
            service.register(s);
            view.displayMessage("시료 등록 완료: " + s.getId() + " - " + s.getName());
        } catch (Exception e) {
            view.displayMessage("[오류] " + e.getMessage());
        }
    }

    private void listAll() {
        view.displaySampleList(service.findAll());
    }

    private void search() {
        String keyword = view.inputSearchKeyword();
        List<Sample> result = service.search(keyword);
        if (result.isEmpty()) {
            view.displayMessage("검색 결과가 없습니다.");
        } else {
            view.displaySampleList(result);
        }
    }
}
