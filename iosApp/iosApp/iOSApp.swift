import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        ZuhlkeLogger.shared.initialize(useSafeInterpolation: true)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
