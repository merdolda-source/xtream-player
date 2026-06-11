// iOS/XtreamPlayer/Common/Utils/Logger.swift
import Foundation
import os.log

class Logger {
    static var isEnabled = true
    private static let subsystem = "com.xtream.player"
    private static let osLog = os.Log(subsystem: subsystem, category: "general")
    private static var fileHandle: FileHandle?
    
    static func setup() {
        #if DEBUG
        isEnabled = true
        #else
        isEnabled = false
        #endif
        
        createLogFile()
    }
    
    static func debug(_ message: String, file: String = #file, function: String = #function, line: Int = #line) {
        log(level: "DEBUG", message: message, file: file, function: function, line: line)
    }
    
    static func info(_ message: String, file: String = #file, function: String = #function, line: Int = #line) {
        log(level: "INFO", message: message, file: file, function: function, line: line)
    }
    
    static func warning(_ message: String, file: String = #file, function: String = #function, line: Int = #line) {
        log(level: "WARNING", message: message, file: file, function: function, line: line)
    }
    
    static func error(_ message: String, file: String = #file, function: String = #function, line: Int = #line) {
        log(level: "ERROR", message: message, file: file, function: function, line: line)
    }
    
    private static func log(level: String, message: String, file: String, function: String, line: Int) {
        guard isEnabled else { return }
        
        let fileName = URL(fileURLWithPath: file).lastPathComponent
        let timestamp = DateFormatter.logDateFormatter.string(from: Date())
        let logMessage = "[\(timestamp)] [\(level)] [\(fileName):\(line)] \(function) - \(message)"
        
        // Console
        os_log("%{public}@", log: osLog, type: logLevelToOSLogType(level), logMessage)
        
        // File
        writeToFile(logMessage)
    }
    
    private static func logLevelToOSLogType(_ level: String) -> OSLogType {
        switch level {
        case "DEBUG":
            return .debug
        case "INFO":
            return .info
        case "WARNING":
            return .warning
        case "ERROR":
            return .error
        default:
            return .default
        }
    }
    
    private static func createLogFile() {
        let logDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let logFileURL = logDirectory.appendingPathComponent("app.log")
        
        if !FileManager.default.fileExists(atPath: logFileURL.path) {
            FileManager.default.createFile(atPath: logFileURL.path, contents: nil)
        }
        
        fileHandle = FileHandle(forWritingAtPath: logFileURL.path)
        fileHandle?.seekToEndOfFile()
    }
    
    private static func writeToFile(_ message: String) {
        guard let fileHandle = fileHandle else { return }
        
        if let data = (message + "\n").data(using: .utf8) {
            fileHandle.write(data)
        }
    }
    
    static func exportLogs() -> URL? {
        let logDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        return logDirectory.appendingPathComponent("app.log")
    }
}

extension DateFormatter {
    static let logDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
        return formatter
    }()
}
