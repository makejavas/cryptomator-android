package org.cryptomator.domain.usecases.vault;

import org.cryptomator.domain.Vault
import org.cryptomator.domain.repository.VaultRepository
import java.util.*

class MoveVaultHelper {

	companion object {
		fun updateVaultPosition(from: Int, to: Int, vaultRepository: VaultRepository): List<Vault> {
			val vaults = vaultRepository.vaults()

			vaults.sortWith(VaultComparator())

			if (from < to) {
				for (i in from until to) {
					Collections.swap(vaults, i, i + 1)
				}
			} else {
				for (i in from downTo to + 1) {
					Collections.swap(vaults, i, i - 1)
				}
			}

			for (i in 0 until vaults.size) {
				vaults[i] = Vault.aCopyOf(vaults[i]).withPosition(i + 1).build()
			}

			vaults.forEach { vault -> vaultRepository.store(vault) }

			return vaults
		}

		fun updateVaultsInDatabase(vaults: List<Vault>, vaultRepository: VaultRepository): List<Vault> {
			vaults.forEach { vault -> vaultRepository.store(vault) }
			return vaultRepository.vaults()
		}
	}

	internal class VaultComparator : Comparator<Vault> {
		override fun compare(o1: Vault, o2: Vault): Int {
			return o1.position - o2.position
		}
	}
}
