locals {
  tags = {
    account = "${data.terraform_remote_state.pl_acrh_state.account_name}"
    environment = "${data.terraform_remote_state.pl_acrh_state.environment}"
  }
}

data "terraform_remote_state" "pl_acrh_state" {
  backend = "azurerm"
  config {
    storage_account_name = "${var.trs_storage_acc_name}"
    container_name       = "${var.trs_container_name}"
    key                  = "${var.trs_key}"
    access_key ="${var.trs_access_key}"
  }
}

module "api_app" {
  source = "git::ssh://git@code.siemens.com/mindsphere-azure-mainline/infrastructure/terraform_modules/api_app.git"

  resource_group_name = "${data.terraform_remote_state.pl_acrh_state.resource_group_name}"
  ase_name = "${data.terraform_remote_state.pl_acrh_state.ase_name}" 
  ase_plan_name = "${data.terraform_remote_state.pl_acrh_state.ase_plan_name}"
  app_site_name = "${var.app_site_name}"

  account_name = "${data.terraform_remote_state.pl_acrh_state.account_name}"
  environment = "${data.terraform_remote_state.pl_acrh_state.environment}"
  connection_strings = [
    {
			name = "account"
			value = "${data.terraform_remote_state.pl_acrh_state.account_name}"
		},
		{
			name = "environment"
			value = "${data.terraform_remote_state.pl_acrh_state.environment}"
		},

    // Add your connection strings here
		{
			name = "sampleConnStr1"
			value = "Sample Value 1"
		},
		{
			name = "sampleConnStr2"
			value = "Sample Value 2"
		}
	]

	appInsightsName="${var.app_site_name}-app-insight"
}