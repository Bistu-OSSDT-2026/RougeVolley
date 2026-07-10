# 贡献指南

感谢你愿意为本项目贡献时间与代码！请先阅读本文档，了解如何参与。

## 行为准则
本项目遵循[贡献者公约](CODE_OF_CONDUCT.md)，请尊重所有参与者。

## 如何提 Issue
- **Bug 报告**：请使用 Bug Report 模板，包含环境信息、复现步骤、期望行为。
- **功能建议**：请先搜索是否已有类似建议，若无则开启 Feature Request。

## 开发环境搭建

### 基本要求
- **JDK**：21 或更高版本（推荐使用 OpenJDK 21）
- **Maven**：3.8 或更高版本
- **Git**：任意较新版本

### 验证本地环境
打开终端，运行以下命令确认已安装且版本符合：

---bash
java --version
# 应该显示类似 openjdk 21.0.x ... 的信息，大版本号 ≥ 21

mvn --version
# 应该显示 Apache Maven 3.8.x 或更高，且 Java version 部分也是 21+

## 提交规范
请使用 [Conventional Commits](https://www.conventionalcommits.org/) 格式：
- `feat: 新功能描述`
- `fix: 修复某个 Bug`
- `docs: 文档修改`
- `style: 代码格式调整（不影响逻辑）`
- `refactor: 重构`
- `test: 添加或修改测试`
- `chore: 构建、工具等杂项`

## Pull Request 流程
1. Fork 仓库并创建新分支，分支名建议：`feature/xxx` 或 `fix/xxx`。
2. 确保通过所有测试。
3. 提交 PR 时填写完整描述，关联对应 Issue（如 Fix #12）。
4. 至少需要一位维护者审核通过后才会合并。

## 代码风格
- 使用 ESLint 和 Prettier，配置见 `.eslintrc.js` 和 `.prettierrc`。
- 提交前请运行 `npm run lint` 检查。

## 许可
贡献的代码将采用本项目的 MIT 许可证。

再次感谢！
