package com.example.ui.strings

import com.example.data.settings.AppLanguage

class AppStrings(val language: AppLanguage) {

    val appTitle: String = when (language) {
        AppLanguage.ENGLISH -> "Secure Note"
        AppLanguage.FRENCH -> "Notes Sécurisées"
        AppLanguage.CHINESE -> "安全便签"
    }

    val appSubtitleEmpty: String = when (language) {
        AppLanguage.ENGLISH -> "No notes"
        AppLanguage.FRENCH -> "Aucune note"
        AppLanguage.CHINESE -> "暂无便签"
    }

    fun appSubtitleCount(count: Int): String = when (language) {
        AppLanguage.ENGLISH -> "$count protected note${if (count > 1) "s" else ""}"
        AppLanguage.FRENCH -> "$count note(s) protégée(s)"
        AppLanguage.CHINESE -> "$count 条加密便签"
    }

    val searchPlaceholder: String = when (language) {
        AppLanguage.ENGLISH -> "Search notes by title..."
        AppLanguage.FRENCH -> "Rechercher par titre..."
        AppLanguage.CHINESE -> "按标题搜索便签..."
    }

    val clearSearch: String = when (language) {
        AppLanguage.ENGLISH -> "Clear search"
        AppLanguage.FRENCH -> "Effacer la recherche"
        AppLanguage.CHINESE -> "清空搜索"
    }

    val emptyVaultTitle: String = when (language) {
        AppLanguage.ENGLISH -> "Your vault is empty"
        AppLanguage.FRENCH -> "Votre coffre est vide"
        AppLanguage.CHINESE -> "您的保密库为空"
    }

    val emptyVaultMessage: String = when (language) {
        AppLanguage.ENGLISH -> "Create your first confidential note by clicking \"New Note\" below."
        AppLanguage.FRENCH -> "Créez votre première note confidentielle en cliquant sur le bouton « Nouvelle Note » ci-dessous."
        AppLanguage.CHINESE -> "点击下方的“新建便签”按钮创建您的第一条加密便签。"
    }

    fun noSearchResults(query: String): String = when (language) {
        AppLanguage.ENGLISH -> "No results found for \"$query\""
        AppLanguage.FRENCH -> "Aucun résultat pour « $query »"
        AppLanguage.CHINESE -> "未找到与“$query”匹配的内容"
    }

    val noSearchResultsTitle: String = when (language) {
        AppLanguage.ENGLISH -> "No notes found"
        AppLanguage.FRENCH -> "Aucune note trouvée"
        AppLanguage.CHINESE -> "未找到便签"
    }

    val newNoteFab: String = when (language) {
        AppLanguage.ENGLISH -> "New Note"
        AppLanguage.FRENCH -> "Nouvelle Note"
        AppLanguage.CHINESE -> "新建便签"
    }

    val settings: String = when (language) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.FRENCH -> "Paramètres"
        AppLanguage.CHINESE -> "设置"
    }

    val cardProtected: String = when (language) {
        AppLanguage.ENGLISH -> "Protected (AES-256)"
        AppLanguage.FRENCH -> "Protégé (AES-256)"
        AppLanguage.CHINESE -> "加密保护 (AES-256)"
    }

    val cardTapToUnlock: String = when (language) {
        AppLanguage.ENGLISH -> "Tap to unlock content"
        AppLanguage.FRENCH -> "Toucher pour déverrouiller"
        AppLanguage.CHINESE -> "点击输入密码解锁"
    }

    val cardUnlocked: String = when (language) {
        AppLanguage.ENGLISH -> "Unlocked"
        AppLanguage.FRENCH -> "Déverrouillé"
        AppLanguage.CHINESE -> "已解锁"
    }

    val cardPin: String = when (language) {
        AppLanguage.ENGLISH -> "Pin"
        AppLanguage.FRENCH -> "Épingler"
        AppLanguage.CHINESE -> "置顶"
    }

    val cardUnpin: String = when (language) {
        AppLanguage.ENGLISH -> "Unpin"
        AppLanguage.FRENCH -> "Désépingler"
        AppLanguage.CHINESE -> "取消置顶"
    }

    val cardLockNow: String = when (language) {
        AppLanguage.ENGLISH -> "Lock now"
        AppLanguage.FRENCH -> "Verrouiller"
        AppLanguage.CHINESE -> "立即锁定"
    }

    val cardDelete: String = when (language) {
        AppLanguage.ENGLISH -> "Delete"
        AppLanguage.FRENCH -> "Supprimer"
        AppLanguage.CHINESE -> "删除"
    }

    val unlockTitle: String = when (language) {
        AppLanguage.ENGLISH -> "Unlock Note"
        AppLanguage.FRENCH -> "Déverrouiller la note"
        AppLanguage.CHINESE -> "解锁便签"
    }

    val unlockPrompt: String = when (language) {
        AppLanguage.ENGLISH -> "Enter the note password to decrypt and view its contents:"
        AppLanguage.FRENCH -> "Saisissez le mot de passe de la note pour déchiffrer son contenu :"
        AppLanguage.CHINESE -> "请输入此便签的密码以解密并查看内容："
    }

    val passwordLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Password"
        AppLanguage.FRENCH -> "Mot de passe"
        AppLanguage.CHINESE -> "密码"
    }

    val passwordHintPrefix: String = when (language) {
        AppLanguage.ENGLISH -> "💡 Hint:"
        AppLanguage.FRENCH -> "💡 Indice :"
        AppLanguage.CHINESE -> "💡 提示："
    }

    val unlockError: String = when (language) {
        AppLanguage.ENGLISH -> "Incorrect password or decryption error"
        AppLanguage.FRENCH -> "Mot de passe incorrect ou erreur de déchiffrement"
        AppLanguage.CHINESE -> "密码错误或解密失败"
    }

    val unlockButton: String = when (language) {
        AppLanguage.ENGLISH -> "Unlock"
        AppLanguage.FRENCH -> "Déverrouiller"
        AppLanguage.CHINESE -> "解锁"
    }

    val cancelButton: String = when (language) {
        AppLanguage.ENGLISH -> "Cancel"
        AppLanguage.FRENCH -> "Annuler"
        AppLanguage.CHINESE -> "取消"
    }

    val createNoteTitle: String = when (language) {
        AppLanguage.ENGLISH -> "New Protected Note"
        AppLanguage.FRENCH -> "Nouvelle note protégée"
        AppLanguage.CHINESE -> "新建加密便签"
    }

    val noteTitleLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Note title"
        AppLanguage.FRENCH -> "Titre de la note"
        AppLanguage.CHINESE -> "便签标题"
    }

    val notePasswordLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Password (required to encrypt)"
        AppLanguage.FRENCH -> "Mot de passe (requis pour chiffrer)"
        AppLanguage.CHINESE -> "密码（用于加密，必填）"
    }

    val passwordRequiredError: String = when (language) {
        AppLanguage.ENGLISH -> "A password is required to encrypt this note"
        AppLanguage.FRENCH -> "Un mot de passe est obligatoire pour chiffrer la note"
        AppLanguage.CHINESE -> "必须设置密码以加密此便签"
    }

    val hintOptionalLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Password hint (optional)"
        AppLanguage.FRENCH -> "Indice pour le mot de passe (optionnel)"
        AppLanguage.CHINESE -> "密码提示（可选）"
    }

    val hintPlaceholder: String = when (language) {
        AppLanguage.ENGLISH -> "e.g. My favorite book..."
        AppLanguage.FRENCH -> "ex. Mon livre favori..."
        AppLanguage.CHINESE -> "例如：我最喜欢的书..."
    }

    val autoSaveLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Auto-save changes"
        AppLanguage.FRENCH -> "Sauvegarde automatique"
        AppLanguage.CHINESE -> "自动保存更改"
    }

    val autoSaveDesc: String = when (language) {
        AppLanguage.ENGLISH -> "Encrypts and saves as you type"
        AppLanguage.FRENCH -> "Chiffre et enregistre au fil de la frappe"
        AppLanguage.CHINESE -> "打字时自动加密并保存到本地"
    }

    val noteColorLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Color accent"
        AppLanguage.FRENCH -> "Couleur de la note"
        AppLanguage.CHINESE -> "便签颜色"
    }

    val createNoteButton: String = when (language) {
        AppLanguage.ENGLISH -> "Create & Encrypt"
        AppLanguage.FRENCH -> "Créer la note"
        AppLanguage.CHINESE -> "创建并加密"
    }

    val deleteNoteTitle: String = when (language) {
        AppLanguage.ENGLISH -> "Delete Note"
        AppLanguage.FRENCH -> "Supprimer la note"
        AppLanguage.CHINESE -> "删除便签"
    }

    val deletePrompt: String = when (language) {
        AppLanguage.ENGLISH -> "To permanently delete this note, please enter its password:"
        AppLanguage.FRENCH -> "Pour confirmer la suppression définitive de cette note, veuillez saisir son mot de passe :"
        AppLanguage.CHINESE -> "为确认永久删除此便签，请输入该便签的密码："
    }

    val deleteError: String = when (language) {
        AppLanguage.ENGLISH -> "Incorrect password: deletion denied"
        AppLanguage.FRENCH -> "Mot de passe incorrect : suppression refusée"
        AppLanguage.CHINESE -> "密码错误：拒绝删除"
    }

    val deletePermanentlyButton: String = when (language) {
        AppLanguage.ENGLISH -> "Delete permanently"
        AppLanguage.FRENCH -> "Supprimer définitivement"
        AppLanguage.CHINESE -> "永久删除"
    }

    val editorTitlePlaceholder: String = when (language) {
        AppLanguage.ENGLISH -> "Note Title..."
        AppLanguage.FRENCH -> "Titre de la note..."
        AppLanguage.CHINESE -> "便签标题..."
    }

    val editorContentPlaceholder: String = when (language) {
        AppLanguage.ENGLISH -> "Type your confidential note here...\nAll data is AES-256 encrypted."
        AppLanguage.FRENCH -> "Écrivez ici votre texte confidentiel...\nToutes les données sont chiffrées en AES-256."
        AppLanguage.CHINESE -> "在此输入您的机密便签内容...\n所有数据均受 AES-256 强加密保护。"
    }

    val editorLockTooltip: String = when (language) {
        AppLanguage.ENGLISH -> "Lock and close"
        AppLanguage.FRENCH -> "Verrouiller et fermer"
        AppLanguage.CHINESE -> "立即锁定并退出"
    }

    val editorSaveTooltip: String = when (language) {
        AppLanguage.ENGLISH -> "Save now"
        AppLanguage.FRENCH -> "Enregistrer maintenant"
        AppLanguage.CHINESE -> "立即保存"
    }

    val editorMenuChangePassword: String = when (language) {
        AppLanguage.ENGLISH -> "Change password"
        AppLanguage.FRENCH -> "Modifier le mot de passe"
        AppLanguage.CHINESE -> "修改密码"
    }

    val editorMenuChangeColor: String = when (language) {
        AppLanguage.ENGLISH -> "Change color"
        AppLanguage.FRENCH -> "Changer la couleur"
        AppLanguage.CHINESE -> "更改颜色"
    }

    val editorMenuDeleteNote: String = when (language) {
        AppLanguage.ENGLISH -> "Delete note"
        AppLanguage.FRENCH -> "Supprimer la note"
        AppLanguage.CHINESE -> "删除此便签"
    }

    val editorAutoSaveDisabled: String = when (language) {
        AppLanguage.ENGLISH -> "Auto-save disabled"
        AppLanguage.FRENCH -> "Sauvegarde auto désactivée"
        AppLanguage.CHINESE -> "自动保存已关闭"
    }

    val editorSaving: String = when (language) {
        AppLanguage.ENGLISH -> "Encrypting & saving..."
        AppLanguage.FRENCH -> "Enregistrement..."
        AppLanguage.CHINESE -> "正在加密并保存..."
    }

    fun editorSavedAt(time: String): String = when (language) {
        AppLanguage.ENGLISH -> "Saved at $time"
        AppLanguage.FRENCH -> "Enregistré à $time"
        AppLanguage.CHINESE -> "已于 $time 保存"
    }

    val editorUnsaved: String = when (language) {
        AppLanguage.ENGLISH -> "Unsaved changes"
        AppLanguage.FRENCH -> "Modifications non enregistrées"
        AppLanguage.CHINESE -> "有未保存的更改"
    }

    fun editorWordCount(words: Int, chars: Int): String = when (language) {
        AppLanguage.ENGLISH -> "$words words  •  $chars characters"
        AppLanguage.FRENCH -> "$words mots  •  $chars caractères"
        AppLanguage.CHINESE -> "$words 词  •  $chars 字符"
    }

    val changePasswordTitle: String = when (language) {
        AppLanguage.ENGLISH -> "Change Note Password"
        AppLanguage.FRENCH -> "Modifier le mot de passe"
        AppLanguage.CHINESE -> "修改便签密码"
    }

    val changePasswordSubtitle: String = when (language) {
        AppLanguage.ENGLISH -> "The note content will be re-encrypted using this new password."
        AppLanguage.FRENCH -> "Le contenu de la note sera rechiffré avec cette nouvelle clé."
        AppLanguage.CHINESE -> "便签内容将使用新密码重新加密。"
    }

    val newPasswordLabel: String = when (language) {
        AppLanguage.ENGLISH -> "New password"
        AppLanguage.FRENCH -> "Nouveau mot de passe"
        AppLanguage.CHINESE -> "新密码"
    }

    val confirmPasswordLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Confirm new password"
        AppLanguage.FRENCH -> "Confirmer le mot de passe"
        AppLanguage.CHINESE -> "确认新密码"
    }

    val passwordMismatchError: String = when (language) {
        AppLanguage.ENGLISH -> "Passwords do not match"
        AppLanguage.FRENCH -> "Les mots de passe ne correspondent pas"
        AppLanguage.CHINESE -> "两次输入的密码不一致"
    }

    val updatePasswordButton: String = when (language) {
        AppLanguage.ENGLISH -> "Update Password"
        AppLanguage.FRENCH -> "Mettre à jour"
        AppLanguage.CHINESE -> "更新密码"
    }

    // Settings screen
    val settingsTitle: String = when (language) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.FRENCH -> "Paramètres"
        AppLanguage.CHINESE -> "设置"
    }

    val sectionGeneral: String = when (language) {
        AppLanguage.ENGLISH -> "Preferences"
        AppLanguage.FRENCH -> "Préférences générales"
        AppLanguage.CHINESE -> "通用偏好"
    }

    val languageLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Language"
        AppLanguage.FRENCH -> "Langue"
        AppLanguage.CHINESE -> "语言"
    }

    val themeLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Theme"
        AppLanguage.FRENCH -> "Thème d'affichage"
        AppLanguage.CHINESE -> "外观主题"
    }

    val themeSystem: String = when (language) {
        AppLanguage.ENGLISH -> "System default"
        AppLanguage.FRENCH -> "Système"
        AppLanguage.CHINESE -> "跟随系统"
    }

    val themeLight: String = when (language) {
        AppLanguage.ENGLISH -> "Light"
        AppLanguage.FRENCH -> "Clair"
        AppLanguage.CHINESE -> "浅色"
    }

    val themeDark: String = when (language) {
        AppLanguage.ENGLISH -> "Dark"
        AppLanguage.FRENCH -> "Sombre"
        AppLanguage.CHINESE -> "深色"
    }

    val fontLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Font"
        AppLanguage.FRENCH -> "Police d'écriture"
        AppLanguage.CHINESE -> "字体样式"
    }

    val fontDefault: String = when (language) {
        AppLanguage.ENGLISH -> "Default (Sans)"
        AppLanguage.FRENCH -> "Par défaut (Sans)"
        AppLanguage.CHINESE -> "默认无衬线 (Sans)"
    }

    val fontSerif: String = when (language) {
        AppLanguage.ENGLISH -> "Serif (Editorial)"
        AppLanguage.FRENCH -> "Serif (Roman)"
        AppLanguage.CHINESE -> "衬线体 (Serif)"
    }

    val fontMonospace: String = when (language) {
        AppLanguage.ENGLISH -> "Monospace (Code)"
        AppLanguage.FRENCH -> "Monospace (Code)"
        AppLanguage.CHINESE -> "等宽字体 (Code)"
    }

    val fontSizeLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Font Size"
        AppLanguage.FRENCH -> "Taille du texte"
        AppLanguage.CHINESE -> "文字大小"
    }

    val fontSizeSmall: String = when (language) {
        AppLanguage.ENGLISH -> "Small"
        AppLanguage.FRENCH -> "Petite"
        AppLanguage.CHINESE -> "小"
    }

    val fontSizeNormal: String = when (language) {
        AppLanguage.ENGLISH -> "Normal"
        AppLanguage.FRENCH -> "Normale"
        AppLanguage.CHINESE -> "标准"
    }

    val fontSizeLarge: String = when (language) {
        AppLanguage.ENGLISH -> "Large"
        AppLanguage.FRENCH -> "Grande"
        AppLanguage.CHINESE -> "大"
    }

    val sectionSecurity: String = when (language) {
        AppLanguage.ENGLISH -> "Cryptographic Verification & Security"
        AppLanguage.FRENCH -> "Preuve Cryptographique & Sécurité"
        AppLanguage.CHINESE -> "密码学验证与安全"
    }

    val inspectDbButton: String = when (language) {
        AppLanguage.ENGLISH -> "Inspect Raw SQLite Data (Verify Encryption)"
        AppLanguage.FRENCH -> "Inspecter la base brute (Preuve de chiffrement)"
        AppLanguage.CHINESE -> "检查原始 SQLite 密文（验证真实加密）"
    }

    val securitySpecs: String = when (language) {
        AppLanguage.ENGLISH -> "• Algorithm: AES-256-GCM (128-bit Auth Tag)\n• Key Derivation: PBKDF2 with 10,000 rounds\n• Storage: 100% Local SQLite (Room)\n• Network: Completely Offline (Zero internet access)\n• AI / Telemetry: None (No Gemini API, no tracking)"
        AppLanguage.FRENCH -> "• Algorithme : AES-256-GCM (Tag d'auth 128-bit)\n• Dérivation : PBKDF2 avec 10 000 itérations\n• Stockage : 100% SQLite local (Room)\n• Réseau : Strictement hors-ligne (Zéro connexion)\n• IA / Télémétrie : Aucune (Pas d'API Gemini, zéro pistage)"
        AppLanguage.CHINESE -> "• 加密算法：AES-256-GCM（128 位认证标签）\n• 密钥派生：PBKDF2（10,000 次哈希迭代）\n• 本地存储：100% 本地 SQLite (Room)\n• 网络权限：无（彻底离线，无网络连接）\n• AI / 追踪：无（绝不使用 Gemini API，无数据外传）"
    }

    val sectionAbout: String = when (language) {
        AppLanguage.ENGLISH -> "About & Open Source"
        AppLanguage.FRENCH -> "À propos & Licence Libre"
        AppLanguage.CHINESE -> "关于与开源许可"
    }

    val aboutLicenseText: String = when (language) {
        AppLanguage.ENGLISH -> "Secure Notes is free and open-source software licensed under the GNU General Public License v3 (GPLv3). Your privacy is guaranteed by mathematics and complete local isolation."
        AppLanguage.FRENCH -> "Notes Sécurisées est un logiciel libre sous licence GNU GPLv3. Votre confidentialité est garantie par les mathématiques et l'isolation locale complète."
        AppLanguage.CHINESE -> "安全便签是一款完全本地的自由开源软件，基于 GNU 通用公共许可证 v3 (GPLv3) 发布。您的隐私由严格的密码学算法及本地隔离提供坚实保障。"
    }

    val inspectDialogTitle: String = when (language) {
        AppLanguage.ENGLISH -> "SQLite Database Inspector"
        AppLanguage.FRENCH -> "Inspecteur Base de Données SQLite"
        AppLanguage.CHINESE -> "SQLite 数据库密文检查器"
    }

    val inspectDialogExplanation: String = when (language) {
        AppLanguage.ENGLISH -> "This proves how your data is actually written to disk in the local SQLite table. As you can see, the text is completely transformed into random ciphertext bytes with a fresh cryptographic salt and initialization vector (IV):"
        AppLanguage.FRENCH -> "Voici la preuve formelle du contenu réellement stocké sur le disque dans la table SQLite. Le texte est entièrement transformé en octets indéchiffrables avec sel et vecteur d'initialisation (IV) aléatoires :"
        AppLanguage.CHINESE -> "这证明了您的数据在手机本地 SQLite 数据库中的真实存储形式。内容已被彻底加密为包含随机盐和初始化向量 (IV) 的不可读密文字符串："
    }

    val inspectRawContentLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Stored 'content' Column (AES-256-GCM Ciphertext):"
        AppLanguage.FRENCH -> "Colonne 'content' brute (Texte chiffré AES-256-GCM) :"
        AppLanguage.CHINESE -> "数据库中存储的 'content' 字段 (AES-256-GCM 密文)："
    }

    val inspectRawPasswordLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Stored 'password' Column (Salted PBKDF2 Hash):"
        AppLanguage.FRENCH -> "Colonne 'password' brute (Hash salé PBKDF2) :"
        AppLanguage.CHINESE -> "数据库中存储的 'password' 字段 (加盐 PBKDF2 哈希)："
    }

    val inspectDecryptedPreviewLabel: String = when (language) {
        AppLanguage.ENGLISH -> "Decrypted in-memory text (only visible when unlocked):"
        AppLanguage.FRENCH -> "Texte déchiffré en mémoire (visible uniquement si déverrouillé) :"
        AppLanguage.CHINESE -> "解密后的内存明文（仅在输入密码解锁后可见）："
    }

    val inspectNoNotes: String = when (language) {
        AppLanguage.ENGLISH -> "Create a note first to inspect its raw ciphertext here!"
        AppLanguage.FRENCH -> "Créez d'abord une note pour inspecter sa vraie valeur chiffrée ici !"
        AppLanguage.CHINESE -> "请先创建一条便签，即可在此查看其底层真实密文！"
    }

    val closeButton: String = when (language) {
        AppLanguage.ENGLISH -> "Close"
        AppLanguage.FRENCH -> "Fermer"
        AppLanguage.CHINESE -> "关闭"
    }

    val backTooltip: String = when (language) {
        AppLanguage.ENGLISH -> "Back"
        AppLanguage.FRENCH -> "Retour"
        AppLanguage.CHINESE -> "返回"
    }
}
