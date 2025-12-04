import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {

    let logsRepository: AppRunsWithLogsRepository

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(logsRepository: logsRepository)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {

    let logsRepository: AppRunsWithLogsRepository

    var body: some View {
        ComposeView(logsRepository: logsRepository)
            .ignoresSafeArea()
    }
}



