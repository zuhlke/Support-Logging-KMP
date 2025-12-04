import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {

    let logsRepository: Logging_coreAppRunsWithLogsRepository

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(logsRepository: logsRepository)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {

    let logsRepository: Logging_coreAppRunsWithLogsRepository

    var body: some View {
        ComposeView(logsRepository: logsRepository)
            .ignoresSafeArea()
    }
}



