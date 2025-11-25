import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        ZuhlkeLogger.shared.initialize(useSafeInterpolation: true, subsystem: "com.zuhlke.logging.sample")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
