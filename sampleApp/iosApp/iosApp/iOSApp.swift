import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    let roomLogWriter = IosRoomLogWriter()

    init() {
        ZuhlkeLogger.shared.initialize(useSafeInterpolation: true, subsystem: "com.zuhlke.logging.sample", logWriters: [roomLogWriter])
    }

    var body: some Scene {
        WindowGroup {
            ContentView(logsRepository: roomLogWriter.repository)
        }
    }
}
