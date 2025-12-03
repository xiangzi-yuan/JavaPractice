package financial.ledger;

import java.math.BigDecimal;

// 一条通用的收支明细，用来展示
public record LedgerItem(
        String id,
        String date,           // 日期：2025-03-01
        String kind,           // "收入" / "支出"
        String typeOrCategory, // "工资" / "食物" / "房租" 等
        BigDecimal amount,     // 金额
        BigDecimal tax,        // 税额（支出填 BigDecimal.ZERO）
        String note            // 备注
) {}
