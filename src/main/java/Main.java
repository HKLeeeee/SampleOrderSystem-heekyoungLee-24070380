import controller.*;
import model.service.*;
import view.*;

import java.util.Scanner;

public class Main {

    private static final String DATA_DIR = "data";

    public static void main(String[] args) {
        AppContext ctx = new AppContext(DATA_DIR);
        Scanner scanner = new Scanner(System.in);

        SampleView sampleView = new SampleView(scanner);
        OrderView orderView = new OrderView(scanner);
        ApprovalView approvalView = new ApprovalView(scanner);
        ProductionView productionView = new ProductionView(scanner);
        MonitoringView monitoringView = new MonitoringView();
        ReleaseView releaseView = new ReleaseView(scanner);
        MainMenuView mainMenuView = new MainMenuView();

        SampleController sampleCtrl = new SampleController(ctx.getSampleService(), sampleView);
        OrderController orderCtrl = new OrderController(ctx.getOrderService(), ctx.getSampleService(), orderView);
        ApprovalController approvalCtrl = new ApprovalController(
                ctx.getApprovalService(), ctx.getOrderService(), ctx.getSampleService(), approvalView);
        ProductionController productionCtrl = new ProductionController(
                ctx.getProductionService(), ctx.getProductionQueue(), productionView);
        MonitoringController monitoringCtrl = new MonitoringController(
                ctx.getMonitoringService(), ctx.getOrderService(), ctx.getSampleService(), monitoringView);
        ReleaseController releaseCtrl = new ReleaseController(
                ctx.getReleaseService(), ctx.getOrderService(), ctx.getSampleService(), releaseView);

        MainController mainCtrl = new MainController(
                mainMenuView, sampleCtrl, orderCtrl, approvalCtrl,
                monitoringCtrl, productionCtrl, releaseCtrl,
                ctx.getSampleService(), ctx.getOrderService(),
                ctx.getProductionQueue(), scanner);

        mainCtrl.run();
    }
}
