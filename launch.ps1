# launch.ps1
# Starts the SmartAgent backend (Jetty on 8081) and the frontend (Vite on 5173)
# together, streaming both logs into this one console. Ctrl+C stops both.
#
# Usage:  powershell -ExecutionPolicy Bypass -File .\launch.ps1
#     or: .\launch.ps1   (from the project root)

$ErrorActionPreference = "Stop"
$root     = $PSScriptRoot
$frontend = Join-Path $root "frontend"

Write-Host ""
Write-Host "Starting SmartAgent ..." -ForegroundColor Cyan
Write-Host "  backend  -> mvn compile exec:java   http://localhost:8081" -ForegroundColor DarkGray
Write-Host "  frontend -> npm run dev             http://localhost:5173" -ForegroundColor DarkGray
Write-Host "  (logs from both appear below; press Ctrl+C to stop both)"   -ForegroundColor Yellow
Write-Host ""

if (-not (Test-Path (Join-Path $frontend "package.json"))) {
    Write-Host "Could not find frontend\package.json next to this script." -ForegroundColor Red
    exit 1
}

$backendProc  = $null
$frontendProc = $null

try {
    # -NoNewWindow lets both children write straight into this console (live logs).
    # cmd /c resolves mvn.cmd / npm.cmd from PATH reliably.
    $backendProc = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c mvn compile exec:java" `
        -WorkingDirectory $root -NoNewWindow -PassThru

    $frontendProc = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c npm run dev" `
        -WorkingDirectory $frontend -NoNewWindow -PassThru

    # Keep the script alive until one of the servers exits (or Ctrl+C).
    while ($true) {
        if ($backendProc.HasExited) {
            Write-Host "`n[backend exited with code $($backendProc.ExitCode)]" -ForegroundColor Red
            break
        }
        if ($frontendProc.HasExited) {
            Write-Host "`n[frontend exited with code $($frontendProc.ExitCode)]" -ForegroundColor Red
            break
        }
        Start-Sleep -Milliseconds 500
    }
}
finally {
    Write-Host "`nStopping servers ..." -ForegroundColor Cyan
    foreach ($p in @($backendProc, $frontendProc)) {
        if ($p -and -not $p.HasExited) {
            # /T kills the whole tree (mvn->java, npm->node)
            taskkill /PID $p.Id /T /F *> $null
        }
    }
    Write-Host "Stopped." -ForegroundColor Cyan
}
