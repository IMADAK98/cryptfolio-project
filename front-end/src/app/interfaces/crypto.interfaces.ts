import { BaseResponse } from './BaseResponse';
import { Action } from './portfolio.interface';

export interface Asset {
  id: number;
  name: string;
  symbol: string;
  totalQuantity: number;
  coinId: string;
  purchaseDate: Date;
  totalValue: number;
  currentPrice: number;
  avgBuyPrice: number;
  realizedProfitLossAmount: number;
  unrealizedProfitLossAmount: number;
  totalProfitAndLossAmount: number;
  transactions: Transactions[];
}

export interface Transactions {
  coinId: string;
  action: Action;
  transactionAmount: number;
  price: number;
  timestamp: Date;
}

export type Coin = {
  id: number;
  symbol: string;
  name: string;
  slug: string;
  rank: number;
  first_historical_data: string;
  last_historical_data: string;
};

export interface CoinResponse {
  id: string;
  name: string;
  symbol: string;
  slug: string;
  quote: {
    USD: {
      price: number;
      volume_24h: number;
      percent_change_1h: number;
      percent_change_24h: number;
      percent_change_7d: number;
      percent_change_30d: number;
      last_updated: string;
      market_cap: number;
    };
  };
  last_updated: string;
}

export interface TransactionRequest {
  coinId: string;
  action: Action;
  amount: number;
  price: number;
  date: Date;
  coinName: string;
  coinSymbol: string;
}

export interface TransactionDto {
  id: number;
  price: number;
  action: Action;
  transactionAmount: number;
  timestamp: Date;
  asset: Asset;
}

export interface UpdateTransactionDto {
  id: number;
  price: number;
  action: Action;
  transactionAmount: number;
  timestamp: Date;
  assetId: number;
}

export interface AssetDto {
  name: string;
  symbol: string;
  totalQuantity: number;
  coinId: string;
  purchaseDate: Date;
  totalValue: number;
  currentPrice: number;
  avgBuyPrice: number;
  realizedProfitLossAmount: number;
  unrealizedProfitLossAmount: number;
  totalProfitAndLossAmount: number;
  unrealizedProfitLossPercentage: number;
  realizedProfitLossPercentage: number;
  piPercentage: number;
  transactions: TransactionDto[];
  initialValue: number;
  totalPlPercentage: number;
}

export interface AssetResponse extends BaseResponse<AssetDto> {}
export interface TransactionsResponse extends BaseResponse<TransactionDto[]> {}
export interface TransactionResponse extends BaseResponse<TransactionDto> {}
export interface DeleteResponse extends BaseResponse<string> {}
