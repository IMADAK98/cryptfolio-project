import { BaseResponse } from './BaseResponse';
import { Asset } from './crypto.interfaces';

export interface Portfolio {
  id: number;
  portfolioName: string;
  userEmail: string;
  assets: Asset[];
  creationDate: Date;
  totalValue: number;
  totalCapitalInvested: number;
  totalProfitAndLossAmount: number;
  totalPlPercent: number;
}

export interface PortfolioRequest {
  portfolioName: string;
}

export enum Action {
  BUY = 'BUY',
  SELL = 'SELL',
}

export interface StatsDto {
  topPerformerCoinId: string;
  topPerformerName: string;
  topPerformerSymbol: string;
  topPerformerPercentChange: number;
  topPerformerValueChange: number;
  worstPerformerCoinId: string;
  worstPerformerName: string;
  worstPerformerSymbol: string;
  worstPerformerPercentChange: number;
  worstPerformerValueChange: number;
}

export interface PortfolioResponse extends BaseResponse<Portfolio> {}
export interface StatsResponse extends BaseResponse<StatsDto> {}
