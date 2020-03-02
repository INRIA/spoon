package org.eevolution.costing;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

import com.google.common.base.Predicates;

import de.metas.costing.CostAmount;
import de.metas.costing.CostElementId;
import de.metas.product.ProductId;
import de.metas.util.GuavaCollectors;
import de.metas.util.lang.RepoIdAware;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;

@ToString
public class BOMCostPrice {
	public static BOMCostPrice empty(@NonNull final ProductId productId) {
		return builder().productId(productId).build();
	}

	@Getter
	private final ProductId productId;
	private final HashMap<CostElementId, BOMCostElementPrice> pricesByElementId;

	@Builder
	private BOMCostPrice(@NonNull final ProductId productId,
			@NonNull @Singular final Collection<BOMCostElementPrice> costElementPrices) {
		this.productId = productId;
		pricesByElementId = costElementPrices.stream()
				.collect(GuavaCollectors.toHashMapByKey(BOMCostElementPrice::getCostElementId));
	}

	public Stream<CostElementId> streamCostElementIds() {
		return pricesByElementId.keySet().stream();
	}

	public BOMCostElementPrice getCostElementPriceOrNull(@NonNull final CostElementId costElementId) {
		return pricesByElementId.get(costElementId);
	}

	public void clearOwnCostPrice(@NonNull final CostElementId costElementId) {
		final BOMCostElementPrice elementCostPrice = getCostElementPriceOrNull(costElementId);
		if (elementCostPrice != null) {
			elementCostPrice.clearOwnCostPrice();
		}
	}

	public void setComponentsCostPrice(@NonNull final CostAmount costPrice,
			@NonNull final CostElementId costElementId) {
		pricesByElementId
				.computeIfAbsent(costElementId, k -> BOMCostElementPrice.zero(costElementId, costPrice.getCurrencyId()))
				.setComponentsCostPrice(costPrice);
	}

	public void clearComponentsCostPrice(@NonNull final CostElementId costElementId) {
		final BOMCostElementPrice elementCostPrice = getCostElementPriceOrNull(costElementId);
		if (elementCostPrice != null) {
			elementCostPrice.clearComponentsCostPrice();
		}
	}

	Collection<BOMCostElementPrice> getElementPrices() {
		return pricesByElementId.values();
	}

	<T extends RepoIdAware> Stream<T> streamIds(@NonNull final Class<T> idType) {
		return getElementPrices().stream()
				.map(BOMCostElementPrice::getId)
				.filter(Predicates.notNull())
				.map(idType::cast);
	}
}
