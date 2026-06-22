<?php
// server/erdin-api/event.php
//
// Matches the POST request RemoteLogger.sendEvent() makes in the GitHub
// Actions generated app (android_build_4.yml -> EVENT_URL). Body is
// application/x-www-form-urlencoded with at least: type, device_id,
// package, version, brand, model, plus event-specific extra fields
// (e.g. name, mode, url, err). Appends one JSON line per event to
// events.log next to this file so you can tail it while testing.

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['ok' => false, 'error' => 'method_not_allowed']);
    exit;
}

const MAX_FIELD_LEN = 500;
const MAX_FIELDS = 30;

$clean = [];
$count = 0;
foreach ($_POST as $key => $value) {
    if ($count >= MAX_FIELDS) {
        break;
    }
    if (!is_string($value)) {
        continue;
    }
    $key = substr(preg_replace('/[^a-zA-Z0-9_]/', '', $key), 0, 64);
    if ($key === '') {
        continue;
    }
    // Strip newlines so one event can't inject extra lines into the log file.
    $value = str_replace(["\r", "\n"], ' ', $value);
    $clean[$key] = mb_substr($value, 0, MAX_FIELD_LEN);
    $count++;
}

$entry = [
    'ts' => date('c'),
    'ip' => $_SERVER['REMOTE_ADDR'] ?? '',
    'data' => $clean,
];

$line = json_encode($entry, JSON_UNESCAPED_SLASHES) . "\n";

$logPath = __DIR__ . '/events.log';
$fh = fopen($logPath, 'a');
if ($fh) {
    flock($fh, LOCK_EX);
    fwrite($fh, $line);
    flock($fh, LOCK_UN);
    fclose($fh);
}

echo json_encode(['ok' => true]);
