# MatrixAntiAFK

Sistema Anti-AFK com detecção inteligente e interface totalmente configurável.

## 📋 Características

- **Detecção Inteligente**: Sistema avançado que detecta tanto inatividade por tempo quanto padrões repetitivos
- **Interface Configurável**: Menu GUI totalmente personalizável através de arquivos YML
- **Compatibilidade Ampla**: Suporte para versões 1.8 até 1.21

## 🚀 Funcionalidades

### Sistema de Detecção

- **Detecção por Tempo**: Monitora inatividade baseada em ausência de movimento, interação e mudanças de câmera
- **Detecção de Padrões**: Identifica comportamentos repetitivos como movimento em círculos ou farm automatizado
- **Configuração Flexível**: Todos os tempos e limites são totalmente configuráveis

### Interface de Desafio

- **Menu Interativo**: GUI customizável com itens aleatórios para verificação humana
- **Timeout Configurável**: Tempo limite ajustável para completar desafios
- **Mensagens Personalizáveis**: Sistema completo de mensagens configuráveis

### Sistema de Punição

- **Múltiplas Opções**: Kick, ban temporário, teleporte ou execução de comandos customizados
- **Comandos Flexíveis**: Integração com outros plugins através de comandos do console
- **Mensagens Customizadas**: Motivos de punição totalmente personalizáveis

## ⚙️ Configuração

### Detecção
```yaml
detection:
  afk-time: 300              # Tempo em segundos para AFK
  check-interval: 10         # Intervalo de verificação
  movement-threshold: 2.0    # Limite de movimento suspeito
  repetitive-limit: 10       # Ações repetitivas antes da suspeita
```

### Menu de Desafio
```yaml
challenge:
  timeout: 30
  menu:
    title: "&cVerificacao Anti-AFK"
    size: 27
    fill-item:
      enabled: true
      material: "GRAY_STAINED_GLASS_PANE"
      name: "&7"
    correct-item:
      name: "&aClique aqui!"
      lore:
        - "&7Prove que voce nao e um bot!"
```

### Punições
```yaml
punishment:
  type: "kick"                    # kick, ban, teleport, command
  ban-command: "ban {player}"     # Para type: ban
  teleport-location: "spawn"      # Para type: teleport
  custom-command: "custom cmd"    # Para type: command
```

## 🎮 Comandos

| Comando | Permissão | Descrição |
|---------|-----------|-----------|
| `/matrixantiafk help` | - | Mostra a ajuda |
| `/matrixantiafk info` | - | Informações do plugin |
| `/matrixantiafk reload` | `matrixantiafk.admin` | Recarrega configurações |
| `/matrixantiafk test` | `matrixantiafk.admin` | Testa o sistema |

## 🔐 Permissões

| Permissão | Descrição |
|-----------|-----------|
| `matrixantiafk.admin` | Acesso aos comandos administrativos |
| `matrixantiafk.bypass` | Ignora a detecção de AFK |
| `matrixantiafk.*` | Todas as permissões |

## 📝 Licença

Este projeto é desenvolvido por **MatrixDev** e está disponível sob licença personalizada.

---

**Desenvolvido com ❤️ por MatrixDev**
