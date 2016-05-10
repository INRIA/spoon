Write-Host "IP: $env:APPVEYOR_HTTP_PROXY_IP"
Write-Host "PORT: $env:APPVEYOR_HTTP_PROXY_PORT"

$mavenConfig = '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                  http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <proxies>
    <proxy>
      <active>true</active>
      <protocol>http</protocol>
      <host>' + $env:APPVEYOR_HTTP_PROXY_IP + '</host>
      <port>' + $env:APPVEYOR_HTTP_PROXY_PORT + '</port>
    </proxy>
  </proxies>
</settings>'

New-Item "$env:USERPROFILE\.m2" -ItemType Directory -Force | Out-Null
Set-Content "$env:USERPROFILE\.m2\settings.xml" -Value $mavenConfig