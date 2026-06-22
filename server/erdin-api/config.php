<?php
// server/erdin-api/config.php
//
// Matches the GET request RemoteConfig.kt makes in the GitHub Actions
// generated app (android_build_4.yml -> CONFIG_URL). Serves the contents
// of config.json as-is, so updating ad unit IDs is just editing that file
// (no redeploy/rebuild of the app needed - it polls this on every launch).

header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');

$configPath = __DIR__ . '/config.json';

if (!is_file($configPath)) {
    // Empty object -> app falls back to Google's test ad IDs (see
    // RemoteConfig.getBannerId/getInterstitialId defaults), never breaks.
    echo '{}';
    exit;
}

$raw = file_get_contents($configPath);
$decoded = json_decode($raw, true);

if (!is_array($decoded)) {
    echo '{}';
    exit;
}

echo json_encode($decoded, JSON_UNESCAPED_SLASHES);
